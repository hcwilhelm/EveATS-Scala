package models.evedump

import models.core.TypesafeID._
import models.core.TypesafeID.driver.simple._
import play.api.libs.json.Json

case class FactionID(untypedID: Long) extends AnyVal with TypedID
object FactionID extends TypedIDCompanion[FactionID]

case class Faction(id: FactionID, name: String, description: String) extends WithID[FactionID]
object Faction { implicit val jsonFormat = Json.format[Faction] }

class FactionTable(tag: Tag) extends IDTable[FactionID, Faction](tag, "chrFactions") {
  override def id = column[FactionID]("factionID", O.PrimaryKey)
  def name = column[String]("factionName")
  def description = column[String]("description")

  override def * = (id, name, description) <> ((Faction.apply _).tupled, Faction.unapply)
}

object FactionTable extends IDRepository[FactionID, Faction, FactionTable](TableQuery[FactionTable])