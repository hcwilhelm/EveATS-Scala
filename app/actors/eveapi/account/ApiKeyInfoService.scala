package actors.eveapi.account

import akka.actor._
import akka.pattern._
import akka.util.Timeout
import models.eveapi.account.ApiKeyInfo
import models.eveapi.eve.Character
import models.eveats.ApiKeyID
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.ws.WS
import service.db.eveapi.account.{ApiKeyInfoService => ApiKeyInfoDBService}
import service.db.eveapi.eve.{CharacterAffiliationService => CharacterAffiliationDBService}
import service.db.eveats.{ApiKeyService => ApiKeyDBService}
import xmlparser.account.ApiKeyInfoParser

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

trait ApiKeyInfoService {
  def getInfo(id: ApiKeyID): Future[ApiKeyInfo]
  def getChars(id: ApiKeyID): Future[Set[Character]]
}

object ApiKeyInfoService {
  val service: ApiKeyInfoService = TypedActor(Akka.system).typedActorOf(TypedProps[ApiKeyInfoServiceImpl], "ApiKeyInfoService")
  def apply() = service
}

private class ApiKeyInfoServiceImpl extends ApiKeyInfoService {
  val actor = TypedActor.context.actorOf(Props[ApiKeyInfoActor] ,"ApiKeyInfoActor")
  implicit val timeout = Timeout(10.seconds)

  override def getInfo(id: ApiKeyID): Future[ApiKeyInfo] = (actor ? id).mapTo[ApiKeyInfo]
  override def getChars(id: ApiKeyID): Future[Set[Character]] = (actor ? id) flatMap { _ =>
    CharacterAffiliationDBService.findCharacters(id)
  }
}

private class ApiKeyInfoActor extends Actor {

  val listeners = mutable.Map[ApiKeyID, Set[ActorRef]]()
  val runningUpdates = mutable.Set[ApiKeyID]()

  val timeout = 10000
  val requestHolder = WS.url("https://api.eveonline.com/Account/ApiKeyInfo.xml.aspx").withRequestTimeout(timeout)

  override def receive = {
    case id: ApiKeyID =>
      listeners(id) = (listeners getOrElse(id, Set.empty[ActorRef])) + sender
      runningUpdates.find(_ == id).fold(find(id))(_ => ())

    case apiKeyInfo: ApiKeyInfo =>
      reply(apiKeyInfo.id, apiKeyInfo)

    case (apiKeyID: ApiKeyID, ex: Exception) =>
      reply(apiKeyID, Status.Failure(ex))
  }

  private def find(id: ApiKeyID): Unit = {
    runningUpdates += id

    ApiKeyInfoDBService.find(id).map {
      case Some(entity) =>
        if (entity.cachedUntil isAfterNow)
          self ! entity
        else
          update(id) recover {
            case ex: Exception => (id, ex)
          } pipeTo self

      case None =>
        update(id) recover {
          case ex: Exception => (id, ex)
        } pipeTo self
    }
  }

  private def update(id: ApiKeyID): Future[ApiKeyInfo] =
    ApiKeyDBService.find(id) flatMap {
      case Some(apiKey) =>
        val request = requestHolder.withQueryString("keyID" -> apiKey.id, "vCode" -> apiKey.vCode)

        request.get() flatMap { response =>
          ApiKeyInfoParser(response.body, id) match {
            case Success((apiKeyInfo, chars, corps)) =>
              for {
                _ <- CharacterAffiliationDBService.insertAffiliation(apiKey.id, chars, corps)
                _ <- ApiKeyInfoDBService.insertOrUpdate(apiKeyInfo)
              } yield apiKeyInfo

            case Failure(ex) => Future.failed(ex)
          }
        }

      case None =>
        Future.failed(new ApiKeyNotFound("ApiKey not found in DB"))
    }

  private def reply(id: ApiKeyID, msg: Any) = {
    listeners(id) map (_ ! msg)
    listeners -= id
    runningUpdates -= id
  }
}
