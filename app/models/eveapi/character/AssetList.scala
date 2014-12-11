package models.eveapi.character

import models.core.TypesafeID.driver.simple._
import models.eveapi.common._
import models.eveapi.eve.{CharacterID, CharacterTable}
import org.joda.time.DateTime
import play.api.libs.json.Json

import scala.slick.model.ForeignKeyAction.Cascade

/**
 * CharacterAssetList
 *
 * @param id
 * @param affiliationID
 * @param createdAt
 * @param cachedUntil
 */
case class CharacterAssetList(id: Option[AssetListID], affiliationID: CharacterID, createdAt: DateTime, cachedUntil: DateTime) extends AssetList[CharacterID]

object CharacterAssetList { implicit val jsonFormat = Json.format[CharacterAssetList] }

/**
 * CharacterAssetListTable
 *
 * @param tag
 */
class CharacterAssetListTable(tag: Tag) extends AssetListTable[CharacterID, CharacterAssetList](tag, "eveats_character_asset_list") {
  def * = (id.?, affiliationID, createdAt, cachedUntil) <> ((CharacterAssetList.apply _).tupled, CharacterAssetList.unapply)
  def affiliation = foreignKey("affiliation_fk", affiliationID, TableQuery[CharacterTable])(_.id, onUpdate = Cascade, onDelete = Cascade)
}

object CharacterAssetListTable extends AssetListRepository[CharacterID, CharacterAssetList, CharacterAssetListTable](TableQuery[CharacterAssetListTable])

/**
 * CharacterAssetItemTable
 *
 * @param tag
 */
class CharacterAssetItemTable(tag: Tag) extends AssetItemTable[CharacterID, CharacterAssetList, CharacterAssetListTable](tag, "eveats_character_asset_item")(TableQuery[CharacterAssetListTable])

object CharacterAssetItemTable extends AssetItemRepository(TableQuery[CharacterAssetItemTable])