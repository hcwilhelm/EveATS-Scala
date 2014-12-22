package actors.eveapi.account

import akka.actor.{ActorRef, Actor, TypedProps, TypedActor}
import akka.util.Timeout
import models.eveapi.account.{AccountStatus, ApiKeyInfo}
import models.eveats.ApiKeyID
import play.api.libs.concurrent.Akka
import play.api.libs.ws.WS
import xmlparser.account.{ApiKeyInfoParser, AccountStatusParser}

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.duration._
import service.db.eveapi.account.{ApiKeyInfoService => ApiKeyInfoDBService, AccountStatusService}
import service.db.eveats.{ApiKeyService => ApiKeyDBService}

import scala.util.{Failure, Success}
import akka.pattern._

trait ApiKeyInfoService {
  def get(id: ApiKeyID): Future[ApiKeyInfo]
}

object ApiKeyInfoService {
  val service: ApiKeyInfoService = TypedActor(Akka.system).typedActorOf(TypedProps[ApiKeyInfoServiceImpl], "ApiKeyInfoService")
  def apply() = service
}

private class ApiKeyInfoServiceImpl extends ApiKeyInfoService {

  implicit val timeout = Timeout(10.seconds)

  override def get(id: ApiKeyID): Future[ApiKeyInfo] = ???
}

private class ApiKeyInfoActor extends Actor {

  val listeners = mutable.Map[ApiKeyID, Set[ActorRef]]()
  val runningUpdates = mutable.Set[ApiKeyID]()

  val timeout = 10000
  val requestHolder = WS.url("https://api.eveonline.com/Account/AccountStatus.xml.aspx").withRequestTimeout(timeout)

  override def receive = {
    case _ => ()
  }

  private def findApiKeyInfo(id: ApiKeyID): Unit = {
    runningUpdates += id

    ApiKeyInfoDBService.find(id).map {
      case Some(entity) =>
        if (entity.cachedUntil isBeforeNow)
          self ! entity
        else
          insertOrUpdate(id) recover {
            case ex: Exception => (id, ex)
          } pipeTo self

      case None =>
        insertOrUpdate(id) recover {
          case ex: Exception => (id, ex)
        } pipeTo self
    }
  }

  private def insertOrUpdate(apiKeyID: ApiKeyID): Future[ApiKeyInfo] =
    ApiKeyDBService.find(apiKeyID) flatMap {
      case Some(apiKey) =>
        val request = requestHolder.withQueryString("keyID" -> apiKey.id, "vCode" -> apiKey.vCode)

        request.get() flatMap { response =>
          ApiKeyInfoParser(response.body, apiKeyID) match {
            case Success((apiKeyInfo, chars, corps)) => ApiKeyInfoDBService.insertOrUpdate(apiKeyInfo) map (_ => apiKeyInfo)
            case Failure(ex) => Future.failed(ex)
          }
        }

      case None =>
        Future.failed(new Exception("ApiKey not found in DB"))
    }
}
