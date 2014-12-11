package models.eveats

import models.core.TypesafeID.driver.simple._
import scala.slick.model.ForeignKeyAction.Cascade

class UsersToApiKeysTable(tag:Tag) extends Table[(UserID, ApiKeyID)](tag, "eveats_users_to_apikeys") {
  def userID = column[UserID]("user_id")
  def apiKeyID = column[ApiKeyID]("apikey_id")

  def user = foreignKey("user_fk", userID, TableQuery[UserTable])(_.id, onUpdate = Cascade, onDelete = Cascade)
  def apiKey = foreignKey("apikey_fk", apiKeyID, TableQuery[ApiKeyTable])(_.id, onUpdate = Cascade, onDelete = Cascade)
  def pk = primaryKey("user_apikey_pk", (userID, apiKeyID))

  def * = (userID, apiKeyID)
}

object UsersToApiKeysTable {
  val query = TableQuery[UsersToApiKeysTable]

  type Row = (UserID, ApiKeyID)

  def findUsers(id: ApiKeyID)(implicit s: Session): Seq[User] =
    query.filter(_.apiKeyID === id).flatMap(_.user).run
  
  def findApiKeys(id: UserID)(implicit s: Session): Seq[ApiKey] =
    query.filter(_.userID === id).flatMap(_.apiKey).run
  
  def insert(row: Row)(implicit s: Session): Int =
    query += row

  def insert(rows: Seq[Row])(implicit s: Session): Option[Int] =
    query ++= rows
  
  def delete(row: Row)(implicit s: Session): Int =
    query.filter(_.userID === row._1).filter(_.apiKeyID === row._2).delete

  def delete(rows: Seq[Row])(implicit s: Session): Int =
    query.filter(_.userID inSet rows.map(_._1)).filter(_.apiKeyID inSet rows.map(_._2)).delete

  def deleteByUser(id: UserID)(implicit s: Session): Int =
    query.filter(_.userID === id).delete

  def deleteByApiKey(id: ApiKeyID)(implicit s: Session): Int =
    query.filter(_.apiKeyID === id).delete
}