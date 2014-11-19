package models.eveapi.character

import models.core.TypesafeID._
import models.core.TypesafeID.driver.simple._
import models.eveapi.eve.{CharacterTable, CharacterID}
import models.evedump.{TypeID, ItemID}
import org.joda.time.DateTime
import play.api.libs.json.Json

import scala.slick.model.ForeignKeyAction.Cascade

/**
 * AssetList
 * 
 * @param untypedID
 */
case class AssetListID(untypedID: Long) extends AnyVal with TypedID
object AssetListID extends TypedIDCompanion[AssetListID]

case class AssetList(id: Option[AssetListID], characterID: CharacterID, createdAt: DateTime, cachedUntil: DateTime) extends WithOptionalID[AssetListID]
object AssetList { implicit val jsonFormat = Json.format[AssetList] }

class AssetListTable(tag: Tag) extends OptionalIDTable[AssetListID, AssetList](tag, "eveats_asset_list") {

  def characterID = column[CharacterID]("character_id")
  def createdAt = column[DateTime]("created_at")
  def cachedUntil = column[DateTime]("cached_until")

  def character = foreignKey("character_fk", characterID, TableQuery[CharacterTable])(_.id, onUpdate = Cascade, onDelete = Cascade)

  def * = (id.?, characterID, createdAt, cachedUntil) <> ((AssetList.apply _).tupled, AssetList.unapply)
}

object AssetListTable extends OptionalIDRepository[AssetListID, AssetList, AssetListTable](TableQuery[AssetListTable])

/**
 * AssetItem
 *
 * @param untypedID
 */
case class AssetItemID(untypedID: Long) extends AnyVal with TypedID
object AssetItemID extends TypedIDCompanion[AssetItemID]

case class AssetItem(
  id: AssetItemID,
  assetListID: AssetListID,
  parentID: Option[AssetItemID],
  locationID: Option[ItemID],
  typeID: TypeID,
  quantity: Int,
  flag: Int,
  singleton: Boolean,
  rawQuantity: Option[Int])

object AssetItem { implicit val jsonFormat = Json.format[AssetItem] }

class AssetItemTable(tag: Tag) extends Table[AssetItem](tag, "eveats_asset_item") {
  def id = column[AssetItemID]("id")
  def assetListID = column[AssetListID]("asset_list_id")
  def parentID = column[Option[AssetItemID]]("parent_id")
  def locationID = column[Option[ItemID]]("location_id")
  def typeID = column[TypeID]("type_id")
  def quantity = column[Int]("quantity")
  def flag = column[Int]("flag")
  def singleton = column[Boolean]("singleton")
  def rawQuantity = column[Option[Int]]("raw_quantity")

  def pk = primaryKey("id_asset_list_pk", (id, assetListID))
  def assetList = foreignKey("asset_list_fk", assetListID, TableQuery[AssetListTable])(_.id, onUpdate = Cascade, onDelete = Cascade)

  def * = (
    id,
    assetListID,
    parentID,
    locationID,
    typeID,
    quantity,
    flag,
    singleton,
    rawQuantity) <> ((AssetItem.apply _).tupled, AssetItem.unapply)
}

object AssetItemTable {
  val query = TableQuery[AssetItemTable]


}