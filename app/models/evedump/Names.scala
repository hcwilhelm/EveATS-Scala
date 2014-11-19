package models.evedump

import models.core.TypesafeID._
import models.core.TypesafeID.driver.simple._
import play.api.libs.json.Json

case class Names(id: ItemID, itemName: String) extends WithID[ItemID]
object Names { implicit val jsonFormat = Json.format[Names] }

class NamesTable(tag: Tag) extends IDTable[ItemID, Names](tag, "invNames") {
  override def id = column[ItemID]("itemID", O.PrimaryKey)
  def itemName = column[String]("itemName")

  def * = (id, itemName) <> ((Names.apply _).tupled, Names.unapply)
}

object NamesTable extends IDRepository[ItemID, Names, NamesTable](TableQuery[NamesTable])

