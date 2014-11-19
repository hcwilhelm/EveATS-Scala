package models.evedump

import models.core.TypesafeID._
import models.core.TypesafeID.driver.simple._
import play.api.libs.json.Json

/**
 * MarketGroupID
 *
 * @param untypedID
 */
case class MarketGroupID(untypedID: Long) extends AnyVal with TypedID
object MarketGroupID extends TypedIDCompanion[MarketGroupID]

/**
 * MarketGroup Entity
 *
 * @param id
 * @param parentGroupID
 * @param marketGroupName
 * @param description
 * @param iconID
 * @param hasTypes
 */
case class MarketGroup(
  id: MarketGroupID,
  parentGroupID: Option[MarketGroupID],
  marketGroupName: String,
  description: Option[String],
  iconID: Option[EveIconID],
  hasTypes: Boolean) extends WithID[MarketGroupID]

object MarketGroup { implicit val jsonFormat = Json.format[MarketGroup] }

/**
 * MarketGroupTable
 *
 * @param tag
 */
class MarketGroupTable(tag: Tag) extends IDTable[MarketGroupID, MarketGroup](tag, "invMarketGroups") {
  override def id = column[MarketGroupID]("marketGroupID", O.PrimaryKey)
  def parentGroupID = column[Option[MarketGroupID]]("parentGroupID")
  def marketGroupName = column[String]("marketGroupName")
  def description = column[Option[String]]("description")
  def iconID = column[Option[EveIconID]]("iconID")
  def hasTypes = column[Boolean]("hasTyped")

  def parent = foreignKey("parent_fk", parentGroupID, TableQuery[MarketGroupTable])(_.id)

  def * = (id, parentGroupID, marketGroupName, description, iconID, hasTypes) <> ((MarketGroup.apply _).tupled, MarketGroup.unapply)
}

object MarketGroupTable extends IDRepository[MarketGroupID, MarketGroup, MarketGroupTable](TableQuery[MarketGroupTable])

/**
 * MarketGroupTree
 *
 */
sealed trait MarketGroupTree
case class MarketGroupNode(marketGroup: MarketGroup, childs: Set[MarketGroupTree]) extends MarketGroupTree

object MarketGroupTree {
  def apply(root: MarketGroup, data: Set[MarketGroup]): MarketGroupTree =
    MarketGroupNode(root, data.filter(_.parentGroupID == Some(root.id)) map (apply(_, data - root)))
}