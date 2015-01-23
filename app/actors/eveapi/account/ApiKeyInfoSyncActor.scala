package actors.eveapi.account

import akka.actor._
import akka.pattern._
import akka.util.Timeout
import models.eveats.ApiKey
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.ws.WS
import play.libs.Akka
import service.db.eveapi.account.{ApiKeyInfoService => ApiKeyInfoDBService}
import service.db.eveapi.eve.{CharacterAffiliationService => AffiliationDBService}
import xmlparser.account.ApiKeyInfoParser

import scala.collection.mutable
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.duration._
import scala.language.postfixOps

class ApiKeyInfoSyncActor extends Actor {
  import actors.eveapi.account.ApiKeyInfoSyncActor._
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

  /**
   * Sync EveApi response to DB and add callbacks
   *
   * @param key
   * @return
   */
  def sync(key: Key): Future[Unit] = {
    val request = requestHolder.withQueryString("keyID" -> key.id, "vCode" -> key.vCode)

    val parseResult = request.get() flatMap { response =>
      ApiKeyInfoParser(response.body, key.id) match {
        case Success((info, chars, corps)) => {

          val updateAffiliation = AffiliationDBService.insertOrUpdateCorporation(corps.toList: _*) andThen {
              case _ => AffiliationDBService.insertOrUpdateCharacter(chars.toList: _*)
            } andThen {
              case _ => ApiKeyInfoDBService.updateCharacters(key.id, chars.toList)
            }

          val updateInfo = ApiKeyInfoDBService.insertOrUpdate(info)

          for {
            _ <- updateAffiliation
            _ <- updateInfo
          } yield ()
        }

        case Failure(ex) =>
          Future.failed(ex)
      }
    }

    parseResult onComplete { case _ => self ! Done(key) }
    parseResult pipeTo sender
  }
}

object ApiKeyInfoSyncActor {

  type Key = ApiKey

  sealed trait Command
  case class Sync(data: Key) extends Command
  case class Done(data: Key) extends Command

  val requestTimeout = 10000
  lazy val requestHolder = WS.url("https://api.eveonline.com/Account/ApiKeyInfo.xml.aspx").withRequestTimeout(requestTimeout)

  implicit val timeout = Timeout(5 seconds)
  lazy val actor = Akka.system.actorOf(Props[ApiKeyInfoSyncActor], "ApiKeyInfoSync")
  def apply(key: ApiKey): Future[Unit] = (actor ? Sync(key)).mapTo[Unit]
}