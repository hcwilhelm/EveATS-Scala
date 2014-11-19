package models.eveats

import models.core.TypesafeID._
import models.core.TypesafeID.driver.simple._
import play.api.libs.json.Json

case class UserID(untypedID: Long) extends AnyVal with TypedID
object UserID extends TypedIDCompanion[UserID]

case class User(id: Option[UserID], email: String, password: String) extends WithOptionalID[UserID]

object User { implicit val jsonFormat = Json.format[User] }

class UserTable(tag: Tag) extends OptionalIDTable[UserID, User](tag, "eveats_users")  {
  def email = column[String]("email")
  def password = column[String]("password")

  override def * = (id.?, email, password) <> ((User.apply _).tupled, User.unapply)
}

object UserTable extends OptionalIDRepository[UserID, User, UserTable](TableQuery[UserTable])