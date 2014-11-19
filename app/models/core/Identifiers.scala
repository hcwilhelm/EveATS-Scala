package models.core

import play.api.libs.json.{Json, JsResult, JsValue, Format}
import scala.slick.lifted.MappedTo

trait Identifiers { self: TypesafeIDCore =>

  trait TypedID extends Any with MappedTo[Long] {
    def untypedID: Long
    override def value: Long = untypedID
  }

  protected trait Applicable[ID <: TypedID] extends Any {
    def apply(id: Long): ID
  }

  protected trait JsonFormatImplicits[ID <: TypedID] { self: Applicable[ID] =>
    implicit final val jsonFormat: Format[ID] = new Format[ID] {
      override def writes(o: ID): JsValue = Json.toJson(o.untypedID)
      override def reads(json: JsValue): JsResult[ID] = json.validate[Long] map apply
    }
  }

  protected trait ConversionImplicits[ID <: TypedID] {
    import scala.language.implicitConversions

    implicit def IDtoLong(id: ID) = id.untypedID
    implicit def IDtoString(id: ID) = id.untypedID.toString
  }

  abstract class TypedIDCompanion[ID <: TypedID] extends Applicable[ID] with JsonFormatImplicits[ID] with ConversionImplicits[ID]

  trait WithOptionalID[ID <: TypedID] {
    def id: Option[ID]
  }

  trait WithID[ID <: TypedID] {
    def id: ID
  }
}
