package models.eveapi.common

import models.core.TypesafeID._
import models.core.TypesafeID.driver.simple._
import org.joda.time.DateTime
import play.api.libs.json.Json

case class Location(
  id: AssetItemID,
  assetListID: AssetListID,
  itemName: String,
  x: Double,
  y: Double,
  z: Double,
  cachedUntil: DateTime) extends WithID[AssetItemID]

object Location { implicit val jsonFormat = Json.format[Location] }

abstract class LocationTable(tag: Tag, tableName: String)
extends IDTable[AssetItemID, Location](tag, tableName) {

  def assetListID = column[AssetListID]("asset_list_id")
  def itemName = column[String]("item_name")
  def x = column[Double]("x")
  def y = column[Double]("y")
  def z = column[Double]("z")
  def cachedUntil = column[DateTime]("cached_until")

  def pk = primaryKey("id_asset_list_id_pk", (id, assetListID))

  override def * = (id, assetListID, itemName, x, y, z, cachedUntil) <> ((Location.apply _).tupled, Location.unapply)
}
