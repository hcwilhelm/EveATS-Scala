package models.eveapi.eve

import models.core.TypesafeID._
import models.core.TypesafeID.driver.simple._
import org.joda.time.DateTime
import play.api.libs.json.Json


/**
 * AffiliationID
 *
 */
sealed trait AffiliationID extends Any with TypedID

/**
 * CharacterID
 *
 * @param untypedID
 */
case class CharacterID(untypedID: Long) extends AnyVal with AffiliationID
object CharacterID extends TypedIDCompanion[CharacterID]

/**
 * CorporationID
 *
 * @param untypedID
 */
case class CorporationID(untypedID: Long) extends AnyVal with AffiliationID
object CorporationID extends TypedIDCompanion[CorporationID]


/**
 * Character
 *
 * @param id
 * @param name
 * @param corporationID
 * @param cachedUntil
 */
case class Character(id: CharacterID, name: String, corporationID: CorporationID, cachedUntil: DateTime) extends WithID[CharacterID]
object Character { implicit val jsonFormat = Json.format[Character] }

/**
 * Corporation
 *
 * @param id
 * @param name
 */
case class Corporation(id: CorporationID, name: String) extends WithID[CorporationID]
object Corporation { implicit val jsonFormat = Json.format[Corporation] }


/**
 * CharacterTable
 *
 * @param tag
 */
class CharacterTable(tag: Tag) extends IDTable[CharacterID, Character](tag, "eveats_character") {

  def name = column[String]("name")
  def corporationID = column[CorporationID]("corporation_id")
  def cachedUntil = column[DateTime]("cached_until")

  def corporation = foreignKey("corporation_fk", corporationID, TableQuery[CorporationTable])(_.id)

  def * = (id, name, corporationID, cachedUntil) <> ((Character.apply _).tupled, Character.unapply)
}

object CharacterTable extends IDRepository[CharacterID, Character, CharacterTable](TableQuery[CharacterTable])

/**
 * CorporationTable
 *
 * @param tag
 */
class CorporationTable(tag: Tag) extends IDTable[CorporationID, Corporation](tag, "eveats_corporation") {

  def name = column[String]("name")

  def * = (id, name) <> ((Corporation.apply _).tupled, Corporation.unapply)
}

object CorporationTable extends IDRepository[CorporationID, Corporation, CorporationTable](TableQuery[CorporationTable])