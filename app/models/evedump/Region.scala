package models.evedump

import models.core.TypesafeID._
import models.core.TypesafeID.driver.simple._
import play.api.libs.json.Json

case class RegionID(untypedID: Long) extends AnyVal with TypedID
object RegionID extends TypedIDCompanion[RegionID]

case class Region(id: RegionID, name: String, x: Double, y: Double, z: Double, xMin: Double, xMax: Double, yMin: Double, yMax: Double, zMin: Double, zMax: Double, factionID: Option[FactionID], radius: Double) extends WithID[RegionID]
object Region { implicit val jsonFormat = Json.format[Region] }

class RegionTable(tag: Tag) extends IDTable[RegionID, Region](tag, "mapRegions") {
  override def id   = column[RegionID]("regionID", O.PrimaryKey)
  def name          = column[String]("regionName")
  def x             = column[Double]("x")
  def y             = column[Double]("y")
  def z             = column[Double]("z")
  def xMin          = column[Double]("xMin")
  def xMax          = column[Double]("xMax")
  def yMin          = column[Double]("yMin")
  def yMax          = column[Double]("yMax")
  def zMin          = column[Double]("zMin")
  def zMax          = column[Double]("zMax")
  def factionID     = column[Option[FactionID]]("factionID")
  def radius        = column[Double]("radius")

  def * = (
    id,
    name,
    x,
    y,
    z,
    xMin,
    xMax,
    yMin,
    yMax,
    zMin,
    zMax,
    factionID,
    radius) <> ((Region.apply _).tupled, Region.unapply)
}

object RegionTable extends IDRepository[RegionID, Region, RegionTable](TableQuery[RegionTable])