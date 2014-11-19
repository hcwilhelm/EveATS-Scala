import org.joda.time.Duration
import play.api.libs.json.{JsResult, Json, JsValue, Format}

package object models {

  trait CustomJsonFormat {

    /**
     * Json Format for joda.time.Duration
     */
    implicit val jodaDurationJsonFormat = new Format[Duration] {
      override def writes(o: Duration): JsValue = Json.toJson(o.getMillis)
      override def reads(json: JsValue): JsResult[Duration] = json.validate[Long] map (new Duration(_))
    }
  }

}
