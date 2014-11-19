package models.evedump

import models.core.TypesafeID._
import models.core.TypesafeID.driver.simple._
import play.api.libs.json.Json

/**
 * RaceID
 *
 * @param untypedID
 */
case class RaceID(untypedID: Long) extends AnyVal with TypedID
object RaceID extends TypedIDCompanion[RaceID]

/**
 * Race Entity
 *
 * @param id
 * @param raceName
 * @param description
 * @param iconID
 * @param shortDescription
 */
case class Race(id: RaceID, raceName: String, description: Option[String], iconID: Option[EveIconID], shortDescription: String) extends WithID[RaceID]
object Race { implicit val jsonFormat = Json.format[Race] }

/**
 * RaceTable
 *
 * @param tag
 */
class RaceTable(tag: Tag) extends IDTable[RaceID, Race](tag, "chrRaces") {
  override def id = column[RaceID]("raceID", O.PrimaryKey)
  def raceName = column[String]("raceName")
  def description = column[Option[String]]("description")
  def iconID = column[Option[EveIconID]]("iconID")
  def shortDescription = column[String]("shortDescription")

  def icon = foreignKey("icon_fk", iconID, TableQuery[EveIconTable])(_.id)

  def * = (id, raceName, description, iconID, shortDescription) <> ((Race.apply _).tupled, Race.unapply)
}

object RaceTable extends IDRepository[RaceID, Race, RaceTable](TableQuery[RaceTable])