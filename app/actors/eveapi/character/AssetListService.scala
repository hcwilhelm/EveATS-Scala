package actors.eveapi.character

import models.eveapi.character.CharacterAssetList
import models.eveapi.eve.CharacterID
import models.eveats.ApiKeyID
import play.api.libs.concurrent.Akka
import play.api.Play.current
import play.api.libs.ws.WS
import scala.collection.mutable
import scala.concurrent.Future
import akka.actor._


trait AssetListService {
  def getList(keyID: ApiKeyID, charID: CharacterID): Future[CharacterAssetList]
}

object AssetListService {
  val service: AssetListService = TypedActor(Akka.system).typedActorOf(TypedProps[AssetListService], "AssetListService")
  def apply() = service
}

private class AssetListServiceImpl extends AssetListService {

  override def getList(keyID: ApiKeyID, charID: CharacterID) = ???
}

private class AssetListActor extends Actor {

  val listeners = mutable.Map[ApiKeyID, Set[ActorRef]]()
  val runningUpdates = mutable.Set[ApiKeyID]()

  val timeout = 10000
  lazy val requestHolder = WS.url("https://api.eveonline.com/Char/AssetList.xml.aspx").withRequestTimeout(timeout)

  override def receive = ???
}