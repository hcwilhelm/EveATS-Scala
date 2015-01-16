package actors.eveapi.character

import akka.actor._
import akka.pattern._
import models.eveapi.eve.{Character, CharacterID}
import models.eveats.{ApiKey, ApiKeyID}
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.ws.WS
import service.db.eveapi.character.AssetListService
import xmlparser.character.AssetListParser
import scala.concurrent.ExecutionContext.Implicits.global

import scala.collection.mutable
import scala.concurrent.Future
import scala.util.{Failure, Success}

sealed trait Command
case class Sync(key: ApiKey, char: Character) extends Command
case class Done(key: ApiKey, char: Character) extends Command

class AssetListSyncActor extends Actor {
  type mapKey = (ApiKeyID, CharacterID)
  val syncMap = mutable.Map[mapKey, Future[Unit]]()

  val timeout = 10000
  lazy val requestHolder = WS.url("https://api.eveonline.com/Char/AssetList.xml.aspx").withRequestTimeout(timeout)
  
  override def receive = {
    // Receive message Sync
    case Sync(key, char) =>
      println("start sync")

      syncMap.get(key.id -> char.id) match {
        case Some(futureSync) => futureSync pipeTo sender

        case None =>
          val futureSync = sync(key, char)

          syncMap += ((key.id, char.id) -> futureSync)

          futureSync onComplete { case _ => self ! Done(key, char) }
          futureSync pipeTo sender
      }

    // Receive message Done
    case Done(key, char) =>
      println("sync Done")
      syncMap -= (key.id -> char.id)
  }

  def sync(key: ApiKey, char: Character): Future[Unit] = {
    val request = requestHolder.withQueryString("keyID" -> key.id, "vCode" -> key.vCode, "characterID" -> char.id)

    request.get() flatMap { response =>
      AssetListParser(response.body) match {
        case Success((partialAssetList, cachedUntil)) =>
          AssetListService.createAssetList(partialAssetList, char, cachedUntil)

        case Failure(ex) =>
          println(ex)
          Future.failed(ex)
      }
    }
  }
}

object AssetListSyncActor {
  lazy val actor = Akka.system.actorOf(Props[AssetListSyncActor], "AssetListSyncActor")
  def apply() = actor
}