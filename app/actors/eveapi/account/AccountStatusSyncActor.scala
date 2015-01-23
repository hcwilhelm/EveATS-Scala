package actors.eveapi.account

import akka.actor._
import akka.pattern._
import akka.util.Timeout
import models.eveats.ApiKey
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.ws.WS
import play.libs.Akka
import service.db.eveapi.account.{AccountStatusService => AccountStatusDBService}
import xmlparser.account.AccountStatusParser

import scala.collection.mutable
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * Created by hcwilhelm on 20.01.15.
 */
class AccountStatusSyncActor extends Actor {
  import actors.eveapi.account.AccountStatusSyncActor._

  val syncMap = mutable.Map[Key, Future[Unit]]()

  override def receive = {
    // Receive message Sync
    case Sync(key) =>
      syncMap.get(key) match {
        case Some(syncJob) => syncJob pipeTo sender()
        case None => syncMap += (key -> sync(key))
      }

    // Receive message Done
    case Done(key) =>
      syncMap -= key
  }

  def sync(key: Key): Future[Unit] = {
    val request = requestHolder.withQueryString("keyID" -> key.id, "vCode" -> key.vCode)

    val parseResult = request.get() flatMap { response =>
      AccountStatusParser(response.body, key.id) match {
        case Success(accountStatus) =>
          AccountStatusDBService.insertOrUpdate(accountStatus).mapTo[Unit]

        case Failure(ex) =>
          Future.failed(ex)
      }
    }

    parseResult onComplete { case _ => self ! Done(key) }
    parseResult pipeTo sender
  }
}

object AccountStatusSyncActor {

  type Key = ApiKey

  sealed trait Command
  case class Sync(data: Key) extends Command
  case class Done(data: Key) extends Command

  val requestTimeout = 10000
  lazy val requestHolder = WS.url("https://api.eveonline.com/Account/AccountStatus.xml.aspx").withRequestTimeout(requestTimeout)

  implicit val timeout = Timeout(5 seconds)
  lazy val actor = Akka.system.actorOf(Props[AccountStatusSyncActor], "AccountStatusSync")
  def apply(key: ApiKey): Future[Unit] = (actor ? Sync(key)).mapTo[Unit]
}