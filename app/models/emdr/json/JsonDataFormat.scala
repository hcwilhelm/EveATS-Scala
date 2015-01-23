package models.emdr.json

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import play.api.libs.json._
import DateTimeJson._

/**
 * Implicit DateTime reads and writes
 */
object DateTimeJson {
  implicit val readsJodaDateTime = Reads[DateTime](js =>
    js.validate[String].map(ISODateTimeFormat.dateTimeParser().parseDateTime)
  )

  implicit val writesJodaDateTime = Writes[DateTime](dt =>
    Json.toJson[String](dt.toString())
  )
}

/**
 * ResultType definition
 */
sealed trait ResultType
case object OrdersType extends ResultType
case object HistoryType extends ResultType

object ResultType {
  def apply(value: String) = value match {
    case "orders" => OrdersType
    case "history" => HistoryType
  }

  def unapply(o: ResultType) = o match {
    case OrdersType => "orders"
    case HistoryType => "history"
  }

  implicit val jsonFormat = new Format[ResultType] {
    override def writes(o: ResultType) = Json.toJson(ResultType.unapply(o))
    override def reads(json: JsValue) = json.validate[String].map(ResultType(_))
  }
}

/**
 * RowType Definitions
 */
sealed trait Row
case class OrderRow(
  price: BigDecimal,
  volRemaining: Long,
  range: Int,
  orderID: Long,
  volEntered: Long,
  minVolume: Int,
  bid: Boolean,
  issueDate: DateTime,
  duration: Int,
  stationID: Long,
  solarSystemID: Option[Long]) extends Row

case class HistoryRow(
  date: DateTime,
  orders: Long,
  quantity: Long,
  low: BigDecimal,
  high: BigDecimal,
  average: BigDecimal) extends Row

object Row {
  implicit val orderRowFormat = new Format[OrderRow] {
    override def writes(o: OrderRow): JsValue =
      JsArray.apply(Seq(
        Json.toJson(o.price),
        Json.toJson(o.volRemaining),
        Json.toJson(o.range),
        Json.toJson(o.orderID),
        Json.toJson(o.volEntered),
        Json.toJson(o.minVolume),
        Json.toJson(o.bid),
        Json.toJson(o.issueDate),
        Json.toJson(o.duration),
        Json.toJson(o.stationID),
        Json.toJson(o.solarSystemID)
      ))

    override def reads(json: JsValue): JsResult[OrderRow] = json.validate[JsArray].map { array =>
      OrderRow(
        array(0).as[BigDecimal],
        array(1).as[Long],
        array(2).as[Int],
        array(3).as[Long],
        array(4).as[Long],
        array(5).as[Int],
        array(6).as[Boolean],
        array(7).as[DateTime],
        array(8).as[Int],
        array(9).as[Long],
        array(10).asOpt[Long]
      )
    }
  }

  implicit val historyRowFormat = new Format[HistoryRow]{
    override def writes(o: HistoryRow): JsValue =
      JsArray.apply(Seq(
        Json.toJson(o.date),
        Json.toJson(o.orders),
        Json.toJson(o.quantity),
        Json.toJson(o.low),
        Json.toJson(o.high),
        Json.toJson(o.average)
      ))

    override def reads(json: JsValue): JsResult[HistoryRow] = json.validate[JsArray].map { array =>
      HistoryRow(
        array(0).as[DateTime],
        array(1).as[Long],
        array(2).as[Long],
        array(3).as[BigDecimal],
        array(4).as[BigDecimal],
        array(5).as[BigDecimal]
      )
    }
  }
}

/**
 * UploadKey
 *
 * @param name
 * @param key
 */
case class UploadKey(name: String, key: String)

object UploadKey {
  implicit val uploadKeyJsonFormat = Json.format[UploadKey]
}

/**
 * Generator
 *
 * @param name
 * @param version
 */
case class Generator(name: String, version: String)

object Generator {
  implicit val generatorJsonFormat = Json.format[Generator]
}

/**
 * RowSet definitions
 *
 * @tparam T
 */
sealed trait RowSet[T] {
  def generatedAt: DateTime
  def regionID: Option[Long]
  def typeID: Long
  def rows: Seq[T]
}

case class OrderRowSet(generatedAt: DateTime, regionID: Option[Long], typeID: Long, rows: Seq[OrderRow]) extends RowSet[OrderRow]
case class HistoryRowSet(generatedAt: DateTime, regionID: Option[Long], typeID: Long, rows: Seq[HistoryRow]) extends RowSet[HistoryRow]

object RowSet {
  implicit val orderRowSetFormat = Json.format[OrderRowSet]
  implicit val historyRowSetFormat = Json.format[HistoryRowSet]
}

/**
 * MessageType definitions
 */
sealed trait Message

case class Order(
  version: String,
  uploadKeys: List[UploadKey],
  generator: Generator,
  currentTime: DateTime,
  rowsets: Seq[OrderRowSet]) extends Message

case class History(
  version: String,
  uploadKeys: List[UploadKey],
  generator: Generator,
  currentTime: DateTime,
  rowsets: Seq[HistoryRowSet]) extends Message

object Message {
  implicit val orderJsonFormat = Json.format[Order]
  implicit val historyJsonFormat = Json.format[History]

  implicit val messageFormat = new Format[Message] {
    override def writes(o: Message) = o match {
      case v: Order => Json.toJson(v)
      case v: History => Json.toJson(v)
    }

    override def reads(json: JsValue) = (json \ "resultType").validate[ResultType].flatMap {
      case OrdersType => json.validate[Order]
      case HistoryType => json.validate[History]
    }
  }
}