package models.evedump

import models.core.TypesafeID._
import models.core.TypesafeID.driver.simple._
import play.api.libs.json.Json

//Todo : Implement Relations

case class MapDenormalize(
  itemID: ItemID,
  typeID: TypeID,
  groupID: GroupID,
  solarSystemID: Option[Int],
  constellationID: Option[Int],
  regionID: Option[Int],
  orbitID: Option[Int],
  x: Double,
  y: Double,
  z: Double,
  radius: Double,
  itemName: String,
  security: Option[Double],
  celestialIndex: Option[Int],
  orbitIndex: Option[Int]) extends WithID[ItemID] {
  override def id: ItemID = itemID
}

object MapDenormalize { implicit val jsonFormat = Json.format[MapDenormalize] }

class MapDenormalizeTable(tag: Tag) extends IDTable[ItemID, MapDenormalize](tag, "mapDenormalize") {
  override def id = column[ItemID]("itemID", O.PrimaryKey)
  def typeID = column[TypeID]("typeID")
  def groupID = column[GroupID]("groupID")
  def solarSystemID = column[Option[Int]]("solarSystemID")
  def constellationID = column[Option[Int]]("constellationID")
  def regionID = column[Option[Int]]("regionID")
  def orbitID = column[Option[Int]]("orbitID")
  def x = column[Double]("x")
  def y = column[Double]("y")
  def z = column[Double]("z")
  def radius = column[Double]("radius")
  def itemName = column[String]("itemName")
  def security = column[Option[Double]]("security")
  def celestialIndex = column[Option[Int]]("celestialIndex")
  def orbitIndex = column[Option[Int]]("orbitIndex")

  def * = (
    id,
    typeID,
    groupID,
    solarSystemID,
    constellationID,
    regionID,
    orbitID,
    x,
    y,
    z,
    radius,
    itemName,
    security,
    celestialIndex,
    orbitIndex) <> ((MapDenormalize.apply _).tupled, MapDenormalize.unapply)
}

object MapDenormalizeTable extends IDRepository[ItemID, MapDenormalize, MapDenormalizeTable](TableQuery[MapDenormalizeTable])