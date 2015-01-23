package models.emdr

import models.core.TypesafeID._
import models.core.TypesafeID.driver.simple._
import models.evedump.{ItemID, RegionID, TypeID}
import org.joda.time.DateTime
import play.api.libs.json.Json

case class OrderID(untypedID: Long) extends AnyVal with TypedID
object OrderID extends TypedIDCompanion[OrderID]

case class Order(
  id: Option[OrderID],
  generatedAt: DateTime,
  regionID: Option[RegionID],
  typeID: TypeID,
  price: BigDecimal,
  volRemaining: Long,
  range: Int,
  orderID: Long,
  volEntered: Long,
  minVolume: Long,
  bid: Boolean,
  issueDate: DateTime,
  duration: Int,
  stationID: ItemID,
  solarSystemID: Option[ItemID]) extends WithOptionalID[OrderID]

object Order { implicit val jsonFormat = Json.format[Order] }

class OrderTable(tag: Tag) extends OptionalIDTable[OrderID, Order](tag, "eveats_emdr_orders") {
  def generatedAt   = column[DateTime]("generated_at")
  def regionID      = column[Option[RegionID]]("region_id")
  def typeID        = column[TypeID]("type_id")
  def price         = column[BigDecimal]("price")
  def volRemaining  = column[Long]("vol_remaining")
  def range         = column[Int]("range")
  def orderID       = column[Long]("order_id")
  def volEntered    = column[Long]("vol_entered")
  def minVolume     = column[Long]("min_volume")
  def bid           = column[Boolean]("bid")
  def issueDate     = column[DateTime]("issue_date")
  def duration      = column[Int]("duration")
  def stationID     = column[ItemID]("station_id")
  def solarSystemID = column[Option[ItemID]]("solar_system_id")

  def orderIndex = index("order_id_idx", orderID, unique = true)

  override def * = (
    id.?,
    generatedAt,
    regionID,
    typeID,
    price,
    volRemaining,
    range,
    orderID,
    volEntered,
    minVolume,
    bid,
    issueDate,
    duration,
    stationID,
    solarSystemID) <> ((Order.apply _).tupled, Order.unapply)
}

object OrderTable extends OptionalIDRepository[OrderID, Order, OrderTable](TableQuery[OrderTable])