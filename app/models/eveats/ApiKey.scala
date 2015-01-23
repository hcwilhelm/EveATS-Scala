package models.eveats

import models.core.TypesafeID._
import models.core.TypesafeID.driver.simple._
import play.api.libs.json.Json

import scala.slick.direct.AnnotationMapper.column

case class ApiKeyID(untypedID: Long) extends AnyVal with TypedID
object ApiKeyID extends TypedIDCompanion[ApiKeyID]

case class ApiKey(id: ApiKeyID, vCode: String) extends WithID[ApiKeyID]
object ApiKey { implicit val jsonFormat = Json.format[ApiKey] }

class ApiKeyTable(tag: Tag) extends IDTable[ApiKeyID, ApiKey](tag, "eveats_apikeys") {
  def vCode = column[String]("vcode")

  override def * = (id, vCode) <> ((ApiKey.apply _).tupled, ApiKey.unapply)
}

object ApiKeyTable extends IDRepository[ApiKeyID, ApiKey, ApiKeyTable](TableQuery[ApiKeyTable])
