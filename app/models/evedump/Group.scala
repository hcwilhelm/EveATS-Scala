package models.evedump

import models.core.TypesafeID._
import models.core.TypesafeID.driver.simple._
import play.api.libs.json.Json

/**
 * GroupID
 *
 * @param untypedID
 */
case class GroupID(untypedID: Long) extends AnyVal with TypedID
object GroupID extends TypedIDCompanion[GroupID]

/**
 * Group Entity
 *
 * @param id
 * @param categoryID
 * @param groupName
 * @param description
 * @param iconID
 * @param useBasePrice
 * @param allowRecycler
 * @param anchored
 * @param anchorable
 * @param fittableNonSingleton
 * @param published
 */
case class Group(
  id: GroupID,
  categoryID: CategoryID,
  groupName: String,
  description: String,
  iconID: EveIconID,
  useBasePrice: Boolean,
  allowManufacture: Boolean,
  allowRecycler: Boolean,
  anchored: Boolean,
  anchorable: Boolean,
  fittableNonSingleton: Boolean,
  published: Boolean) extends WithID[GroupID]

object Group { implicit val jsonFormat = Json.format[Group] }

/**
 * GroupTable
 *
 * @param tag
 */
class GroupTable(tag: Tag) extends IDTable[GroupID, Group](tag, "invGroups") {
  override def id             = column[GroupID]("groupID", O.PrimaryKey)
  def categoryID              = column[CategoryID]("categoryID")
  def groupName               = column[String]("groupName")
  def description             = column[String]("description")
  def iconID                  = column[EveIconID]("iconID")
  def useBasePrice            = column[Boolean]("useBasePrice")
  def allowManufacture        = column[Boolean]("allowManufacture")
  def allowRecycler           = column[Boolean]("allowRecycler")
  def anchored                = column[Boolean]("anchored")
  def anchorable              = column[Boolean]("anchorable")
  def fittableNonSingleton    = column[Boolean]("fittableNonSingleton")
  def published               = column[Boolean]("published")

  def category = foreignKey("category_fk", categoryID, TableQuery[CategoryTable])(_.id)
  def icon = foreignKey("icon_fk", iconID, TableQuery[EveIconTable])(_.id)

  def * = (id, categoryID, groupName, description, iconID, useBasePrice, allowManufacture, allowRecycler, anchored, anchorable, fittableNonSingleton, published) <> ((Group.apply _).tupled, Group.unapply)
}