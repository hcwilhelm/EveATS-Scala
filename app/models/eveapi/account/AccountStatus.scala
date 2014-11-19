package models.eveapi.account

import models.core.TypesafeID._
import models.core.TypesafeID.driver.simple._
import models.eveats.{ApiKeyTable, ApiKeyID}
import org.joda.time.{Duration, DateTime}
import play.api.libs.json.Json
import scala.slick.model.ForeignKeyAction.Cascade
import models.CustomJsonFormat


case class AccountStatus(id: ApiKeyID, paidUntil: DateTime, createdAt: DateTime, logonCount: Int, logonDuration: Duration, cachedUntil: DateTime) extends WithID[ApiKeyID]
object AccountStatus extends CustomJsonFormat { implicit val jsonFormat = Json.format[AccountStatus] }

class AccountStatusTable(tag: Tag) extends IDTable[ApiKeyID, AccountStatus](tag, "eveats_account_status") {

  def paidUntil = column[DateTime]("paid_until")
  def createdAt = column[DateTime]("created_at")
  def logonCount = column[Int]("logon_count")
  def logonDuration = column[Duration]("logon_duration")
  def cachedUntil = column[DateTime]("cached_until")

  def apiKey = foreignKey("apikey_fk", id, TableQuery[ApiKeyTable])(_.id, onUpdate = Cascade, onDelete = Cascade)

  def * = (id, paidUntil, createdAt, logonCount, logonDuration, cachedUntil) <> ((AccountStatus.apply _).tupled, AccountStatus.unapply)
}

object AccountStatusTable extends IDRepository[ApiKeyID, AccountStatus, AccountStatusTable](TableQuery[AccountStatusTable])