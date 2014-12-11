package models.eveapi.corporation

import models.core.TypesafeID._
import models.core.TypesafeID.driver.simple._
import models.eveapi.common.{Location, AssetItemID, LocationTable}

import scala.slick.model.ForeignKeyAction.Cascade

/**
 * CorporationLocationTable
 *
 * @param tag
 */
class CorporationLocationTable(tag: Tag) extends LocationTable(tag, "eveats_corporation_location") {
  def assetItem = foreignKey("asset_item_fk", id, TableQuery[CorporationAssetItemTable])(_.id, onUpdate = Cascade, onDelete = Cascade)
  def assetList = foreignKey("asset_list_fk", assetListID, TableQuery[CorporationAssetListTable])(_.id, onUpdate = Cascade, onDelete = Cascade)
}

object CorporationLocationTable extends IDRepository[AssetItemID, Location, CorporationLocationTable](TableQuery[CorporationLocationTable])

