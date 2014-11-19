package models.evedump

import models.core.TypesafeID._
import models.core.TypesafeID.driver.simple._
import play.api.libs.json.Json

/**
 * EveIconID
 *
 * @param untypedID
 */
case class EveIconID(untypedID: Long) extends AnyVal with TypedID
object EveIconID extends TypedIDCompanion[EveIconID]

/**
 * EveIcon Entity
 *
 * @param id
 * @param iconFile
 * @param description
 */
case class EveIcon(id: EveIconID, iconFile: String, description: String) extends WithID[EveIconID]
object EveIcon { implicit val jsonFormat = Json.format[EveIcon] }

/**
 * EveIcon Table
 *
 * @param tag
 */
class EveIconTable(tag: Tag) extends IDTable[EveIconID, EveIcon](tag, "EveIcons") {
  override def id = column[EveIconID]("iconID", O.PrimaryKey)
  def iconFile = column[String]("iconFile")
  def description = column[String]("description")

  def * = (id, iconFile, description) <> ((EveIcon.apply _).tupled, EveIcon.unapply)
}

object EveIconTable extends IDRepository[EveIconID, EveIcon, EveIconTable](TableQuery[EveIconTable])