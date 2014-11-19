package models.eveapi.eve

import models.core.TypesafeID.driver.simple._
import models.eveats.{ApiKey, ApiKeyTable, ApiKeyID}

import scala.slick.model.ForeignKeyAction.Cascade

class CharactersToApiKeys(tag: Tag) extends Table[(CharacterID, ApiKeyID)](tag, "eveats_characters_to_apikeys") {
  def characterID = column[CharacterID]("character_id")
  def apiKeyID = column[ApiKeyID]("apikey_id")

  def pk = primaryKey("character_apikey_pk", (characterID, apiKeyID))
  def character = foreignKey("character_fk", characterID, TableQuery[CharacterTable])(_.id, onUpdate = Cascade, onDelete = Cascade)
  def apiKey = foreignKey("apikey_fk", apiKeyID, TableQuery[ApiKeyTable])(_.id, onUpdate = Cascade, onDelete = Cascade)

  def * = (characterID, apiKeyID)
}

object CharactersToApiKeys {
  val query = TableQuery[CharactersToApiKeys]

  type Row = (CharacterID, ApiKeyID)

  def findCharacters(id: ApiKeyID)(implicit s: Session): Seq[Character] =
    query.filter(_.apiKeyID === id).flatMap(_.character).run

  def findApiKeys(id: CharacterID)(implicit s: Session): Seq[ApiKey] =
    query.filter(_.characterID === id).flatMap(_.apiKey).run

  def insert(row: Row)(implicit s: Session): Int =
    query += row
  
  def insert(rows: Seq[Row])(implicit s: Session): Option[Int] =
    query ++= rows

  def delete(row: Row)(implicit s: Session): Int =
    query.filter(_.characterID === row._1).filter(_.apiKeyID === row._2).delete

  def delete(rows: Seq[Row])(implicit s: Session): Int =
    query.filter(_.characterID inSet rows.map(_._1)).filter(_.apiKeyID inSet rows.map(_._2)).delete

  def deleteByCharacter(id: CharacterID)(implicit s: Session): Int =
    query.filter(_.characterID === id).delete

  def deleteByApiKey(id: ApiKeyID)(implicit s: Session): Int =
    query.filter(_.apiKeyID === id).delete
}