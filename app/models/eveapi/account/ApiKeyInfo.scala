package models.eveapi.account

import models.core.TypesafeID._
import models.core.TypesafeID.driver.simple._
import models.eveats.{ApiKeyTable, ApiKeyID}
import org.joda.time.DateTime
import play.api.libs.json.{Json, JsResult, JsValue, Format}

import scala.slick.model.ForeignKeyAction.Cascade

/**
 * KeyType
 */
sealed trait KeyType
case object Account extends KeyType
case object Character extends KeyType
case object Corporation extends KeyType

object KeyType {

  def apply(value: String) = value match {
    case "Account"      => Account
    case "Character"    => Character
    case "Corporation"  => Corporation
  }

  def unapply(value: KeyType) = value.toString

  implicit val jsonKeyTypeFormat = new Format[KeyType] {
    override def reads(json: JsValue): JsResult[KeyType] = json.validate[String].map(KeyType(_))
    override def writes(o: KeyType): JsValue = Json.toJson(o.toString)
  }
}

/**
 * ApiKeyInfo
 *
 * @param id
 * @param accessMask
 * @param keyType
 * @param expires
 * @param cachedUntil
 */
case class ApiKeyInfo(id: ApiKeyID, accessMask: Int, keyType: KeyType, expires: Option[DateTime], cachedUntil: DateTime) extends WithID[ApiKeyID]
object ApiKeyInfo { implicit val jsonApiKeyInfoFormat = Json.format[ApiKeyInfo] }

/**
 * ApiKeyTable
 *
 * @param tag
 */
class ApiKeyInfoTable(tag: Tag) extends IDTable[ApiKeyID, ApiKeyInfo](tag, "eveats_apikey_info") {

  implicit val keyTypeColumnTypeMapper = MappedColumnType.base[KeyType, String](_.toString, KeyType(_))

  def accessMask = column[Int]("access_mask")
  def keyType = column[KeyType]("key_type")
  def expires = column[Option[DateTime]]("expires")
  def cachedUntil = column[DateTime]("cached_until")

  def apiKey = foreignKey("apikey_fk", id, TableQuery[ApiKeyTable])(_.id, onUpdate = Cascade, onDelete = Cascade)

  def * = (id, accessMask, keyType, expires, cachedUntil) <> ((ApiKeyInfo.apply _).tupled, ApiKeyInfo.unapply)
}

object ApiKeyInfoTable extends IDRepository[ApiKeyID, ApiKeyInfo, ApiKeyInfoTable](TableQuery[ApiKeyInfoTable])