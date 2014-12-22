package models.eveapi.corporation

import models.core.TypesafeID.driver.simple._
import models.eveapi.common._
import models.eveapi.eve.{CorporationID, CorporationTable}
import org.joda.time.DateTime
import play.api.libs.json.Json

import scala.slick.model.ForeignKeyAction.Cascade

/**
 * CorporationAssetList
 *
 * @param id
 * @param affiliationID
 * @param createdAt
 * @param cachedUntil
 */
case class CorporationAssetList(id: Option[AssetListID], affiliationID: CorporationID, createdAt: DateTime, cachedUntil: DateTime) extends AssetList[CorporationID]

object CorporationAssetList { implicit val jsonFormat = Json.format[CorporationAssetList] }

/**
 * CorporationAssetListTable
 *
 * @param tag
 */
class CorporationAssetListTable(tag: Tag) extends AssetListTable[CorporationID, CorporationAssetList](tag, "eveats_corporation_asset_list") {
  def * = (id.?, affiliationID, createdAt, cachedUntil) <> ((CorporationAssetList.apply _).tupled, CorporationAssetList.unapply)
  def affiliation = foreignKey("corporation_affiliation_fk", affiliationID, TableQuery[CorporationTable])(_.id, onUpdate = Cascade, onDelete = Cascade)
}

object CorporationAssetListTable extends AssetListRepository[CorporationID, CorporationAssetList, CorporationAssetListTable](TableQuery[CorporationAssetListTable])

/**
 * CorporationAssetItemTable
 *
 * @param tag
 */
class CorporationAssetItemTable(tag: Tag) extends AssetItemTable[CorporationID, CorporationAssetList, CorporationAssetListTable](tag, "eveats_corporation_asset_item")(TableQuery[CorporationAssetListTable])

object CorporationAssetItemTable extends AssetItemRepository(TableQuery[CorporationAssetItemTable])