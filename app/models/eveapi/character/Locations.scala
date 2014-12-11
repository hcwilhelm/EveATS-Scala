package models.eveapi.character

import models.core.TypesafeID._
import models.core.TypesafeID.driver.simple._
import models.eveapi.common.{Location, AssetItemID, LocationTable}

import scala.slick.model.ForeignKeyAction.Cascade

/**
 * CharacterLocationTable
 *
 * @param tag
 */
class CharacterLocationTable(tag: Tag) extends LocationTable(tag, "eveats_character_location") {
  def assetItem = foreignKey("asset_item_fk", id, TableQuery[CharacterAssetItemTable])(_.id, onUpdate = Cascade, onDelete = Cascade)
  def assetList = foreignKey("asset_list_fk", assetListID, TableQuery[CharacterAssetListTable])(_.id, onUpdate = Cascade, onDelete = Cascade)
}

object CharacterLocationTable extends IDRepository[AssetItemID, Location, CharacterLocationTable](TableQuery[CharacterLocationTable])
