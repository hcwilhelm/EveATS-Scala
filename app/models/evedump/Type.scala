package models.evedump

import models.core.TypesafeID._
import models.core.TypesafeID.driver.simple._
import play.api.libs.json.Json

import scala.slick.lifted.ForeignKeyQuery


/**
 * TypeID
 *
 * @param untypedID
 */
case class TypeID(untypedID: Long) extends AnyVal with TypedID
object TypeID extends TypedIDCompanion[TypeID]

/**
 * Type Entity
 *
 * @param id
 * @param groupID
 * @param description
 * @param mass
 * @param volume
 * @param capacity
 * @param portionSize
 * @param raceID
 * @param basePrice
 * @param published
 * @param marketGroupID
 * @param chanceOfDuplicating
 */
case class Type(
  id: TypeID,
  groupID: GroupID,
  typeName: String,
  description: Option[String],
  mass: Double,
  volume: Double,
  capacity: Double,
  portionSize: Int,
  raceID: Option[RaceID],
  basePrice: BigDecimal,
  published: Boolean,
  marketGroupID: Option[MarketGroupID],
  chanceOfDuplicating: Double) extends WithID[TypeID]

object Type { implicit val jsonFormat = Json.format[Type] }

/**
 * TypeTable
 *
 * @param tag
 */
class TypeTable(tag: Tag) extends IDTable[TypeID, Type](tag, "invTypes") {
  override def id           = column[TypeID]("typeID", O.PrimaryKey)
  def groupID               = column[GroupID]("groupID")
  def typeName              = column[String]("typeName")
  def description           = column[Option[String]]("description")
  def mass                  = column[Double]("mass")
  def volume                = column[Double]("volume")
  def capacity              = column[Double]("capacity")
  def portionSize           = column[Int]("portionSize")
  def raceID                = column[Option[RaceID]]("raceID")
  def basePrice             = column[BigDecimal]("basePrice")
  def published             = column[Boolean]("published")
  def marketGroupID         = column[Option[MarketGroupID]]("marketGroupID")
  def chanceOfDuplicating   = column[Double]("chanceOfDuplicating")

  def group = foreignKey("group_fk", groupID, TableQuery[GroupTable])(_.id)

  def * = (
    id,
    groupID,
    typeName,
    description,
    mass,
    volume,
    capacity,
    portionSize,
    raceID,
    basePrice,
    published,
    marketGroupID,
    chanceOfDuplicating) <> ((Type.apply _).tupled, Type.unapply)
}

object TypeTable extends IDRepository[TypeID, Type, TypeTable](TableQuery[TypeTable])