package actors.eveapi.character

import akka.actor._
import akka.pattern._
import akka.util.Timeout
import models.eveapi.eve.Character
import models.eveats.ApiKey
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.ws.WS
import service.db.eveapi.character.AssetListService
import xmlparser.character.AssetListParser

import scala.collection.mutable
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.duration._
import scala.language.postfixOps

class AssetListSyncActor extends Actor {
  import actors.eveapi.character.AssetListSyncActor._
  val syncMap = mutable.Map[Key, Future[Unit]]()

  override def receive = {
    // Receive message Sync
    case Sync(key) =>
      syncMap.get(key) match {
        case Some(syncJob) => syncJob pipeTo sender
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
    val (apiKey, char) = key
    val request = requestHolder.withQueryString("keyID" -> apiKey.id, "vCode" -> apiKey.vCode, "characterID" -> char.id)

    val parseResult = request.get() flatMap { response =>
      AssetListParser(response.body) match {
        case Success((partialAssetList, cachedUntil)) =>
          AssetListService.createAssetList(partialAssetList, char, cachedUntil)

        case Failure(ex) =>
          Future.failed(ex)
      }
    }
    
    parseResult onComplete { case _ => self ! Done(key) }
    parseResult pipeTo sender
  }
}

object AssetListSyncActor {

  type Key = (ApiKey, Character)

  sealed trait Command
  case class Sync(data: Key) extends Command
  case class Done(data: Key) extends Command

  val requestTimeout = 10000
  lazy val requestHolder = WS.url("https://api.eveonline.com/Char/AssetList.xml.aspx").withRequestTimeout(requestTimeout)

  implicit val timeout = Timeout(5 seconds)
  lazy val actor = Akka.system.actorOf(Props[AssetListSyncActor], "AssetListSync")
  def apply(key: ApiKey, char: Character): Future[Unit] = (actor ? Sync(key -> char)).mapTo[Unit]
}