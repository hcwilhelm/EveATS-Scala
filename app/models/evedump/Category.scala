package models.evedump

import models.core.TypesafeID._
import models.core.TypesafeID.driver.simple._
import play.api.libs.json.Json

/**
 * CategoryID
 *
 * @param untypedID
 */
case class CategoryID(untypedID: Long) extends AnyVal with TypedID
object CategoryID extends TypedIDCompanion[CategoryID]

/**
 * Category Entity
 *
 * @param id
 * @param categoryName
 * @param description
 * @param iconID
 * @param published
 */
case class Category(id: CategoryID, categoryName: String, description: String, iconID: EveIconID, published: Boolean) extends WithID[CategoryID]
object Category { implicit val jsonFormat = Json.format[Category] }

/**
 * CategoryTable
 *
 * @param tag
 */
class CategoryTable(tag: Tag) extends IDTable[CategoryID, Category](tag, "invCategories") {
  override def id     = column[CategoryID]("categoryID", O.PrimaryKey)
  def categoryName    = column[String]("categoryName")
  def description     = column[String]("description")
  def iconID          = column[EveIconID]("iconID")
  def published       = column[Boolean]("published")

  def icon = foreignKey("icon_fk", iconID, TableQuery[EveIconTable])(_.id)

  def * = (id, categoryName, description, iconID, published) <> ((Category.apply _).tupled, Category.unapply)
}

object CategoryTable extends IDRepository[CategoryID, Category, CategoryTable](TableQuery[CategoryTable])