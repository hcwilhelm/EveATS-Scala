package actors.eveapi.account

import akka.actor._
import akka.pattern._
import akka.util.Timeout
import models.eveapi.account.AccountStatus
import models.eveats.ApiKeyID
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.ws.WS
import service.db.eveapi.account.{AccountStatusService => AccountStatusDBService}
import service.db.eveats.{ApiKeyService => ApiKeyDBService}
import xmlparser.account.AccountStatusParser

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
 * AccountStatusService
 */
trait AccountStatusService {
  def get(apiKeyID: ApiKeyID): Future[AccountStatus]
}

object AccountStatusService {
  val service: AccountStatusService = TypedActor(Akka.system).typedActorOf(TypedProps[AccountStatusServiceImpl], "AccountStatusService")
  def apply() = service
}

/**
 * AccountStatusService Implementation
 */
private class AccountStatusServiceImpl extends AccountStatusService {
  private[this] val actor = TypedActor.context.actorOf(Props[AccountStatusActor] ,"AccountStatusActor")

  implicit val timeout = Timeout(10.seconds)

  override def get(apiKeyID: ApiKeyID): Future[AccountStatus] = (actor ? apiKeyID).mapTo[AccountStatus]
}

/**
 * AccountStatus Actor
 */
private class AccountStatusActor extends Actor {

  val listeners = mutable.Map[ApiKeyID, Set[ActorRef]]()
  val runningUpdates = mutable.Set[ApiKeyID]()

  val timeout = 10000
  val requestHolder = WS.url("https://api.eveonline.com/Account/AccountStatus.xml.aspx").withRequestTimeout(timeout)

  override def receive = {
    case apiKeyID: ApiKeyID =>
      listeners(apiKeyID) = (listeners getOrElse(apiKeyID, Set.empty[ActorRef])) + sender
      runningUpdates.find(_ == apiKeyID).fold(findAccountStatus(apiKeyID))(_ => ())

    case accountStatus: AccountStatus =>
      reply(accountStatus.id, accountStatus)

    case (apiKeyID: ApiKeyID, ex: Exception) =>
      reply(apiKeyID, Status.Failure(ex))
  }

  private def findAccountStatus(id: ApiKeyID): Unit =  {
    runningUpdates += id

    AccountStatusDBService.find(id).map {
      case Some(accountStatus) =>
        if (accountStatus.cachedUntil isBeforeNow)
          self ! accountStatus
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

  private def insertOrUpdate(apiKeyID: ApiKeyID): Future[AccountStatus] =
    ApiKeyDBService.find(apiKeyID) flatMap {
      case Some(apiKey) =>
        val request = requestHolder.withQueryString("keyID" -> apiKey.id, "vCode" -> apiKey.vCode)

        request.get() flatMap { response =>
          AccountStatusParser(response.body, apiKeyID) match {
            case Success(accountStatus) => AccountStatusDBService.insertOrUpdate(accountStatus) map (_ => accountStatus)
            case Failure(ex) => Future.failed(ex)
          }
        }

      case None =>
        Future.failed(new Exception("ApiKey not found in DB"))
    }

  private def reply(id: ApiKeyID, msg: Any) = {
    listeners(id) map (_ ! msg)
    listeners -= id
    runningUpdates -= id
  }
}
