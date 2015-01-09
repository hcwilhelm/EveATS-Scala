package actors.eveapi.character

import models.eveapi.character.CharacterAssetList
import models.eveapi.eve.CharacterID
import play.api.libs.concurrent.Akka

import scala.concurrent.Future
import akka.actor._


trait AssetListService {
  def get(id: CharacterID): Future[CharacterAssetList]
}

object AssetListService {
  val service: AssetListService = TypedActor(Akka.system).typedActorOf(TypedProps[AssetListService], "AssetListService")
}
