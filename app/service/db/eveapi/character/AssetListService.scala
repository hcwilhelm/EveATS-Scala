package service.db.eveapi.character

import models.eveapi.character.{CharacterAssetItemTable, CharacterAssetList, CharacterAssetListTable}
import models.eveapi.common.{AssetItem, AssetListID}
import models.eveapi.eve.Character
import org.joda.time.DateTime
import play.api.Play.current
import play.api.db.slick.DB
import service.db.executionContext

import scala.concurrent._

object AssetListService {

  def createAssetList(partialAssetList: Seq[AssetListID => AssetItem], char: Character, cachedUntil: DateTime): Future[Unit] = Future {
    DB.withSession { implicit session =>
      val assetListID = CharacterAssetListTable.insert(CharacterAssetList(None, char.id, DateTime.now(), cachedUntil))
      val items = partialAssetList map (_.apply(assetListID))
      CharacterAssetItemTable.insert(items: _*)
    }
  }
}
