
import java.io.StringReader
import javax.xml.namespace.QName

import com.fasterxml.aalto.stax.InputFactoryImpl
import org.joda.time.format

package object xmlparser {

  /**
   * DateTimeFormatter for EVEApi XML data
   */
  val dateTimeFormatter = format.DateTimeFormat.forPattern("Y-M-d H:m:s").withZoneUTC()

  /**
   * Stax EventReader factory
   */
  object EventReaderFactory {
    def apply(body: String): javax.xml.stream.XMLEventReader = new InputFactoryImpl().createXMLEventReader(new StringReader(body))
  }

  /**
   * XMLParserExceptions
   */
  sealed trait XMLParserException extends RuntimeException
  case class EveApiError(val code: Int, message: String) extends XMLParserException
  case class InvalidXML(message: String) extends XMLParserException

  /**
   * EveApi xml QNames
   */
  object QNames {
    val KEY = new QName("key")
    val ACCESS_MASK = new QName("accessMask")
    val TYPE = new QName("type")
    val EXPIRES = new QName("expires")
    val CACHED_UNTIL = new QName("cachedUntil")

    val PAID_UNTIL = new QName("paidUntil")
    val CREATE_DATE = new QName("createDate")
    val LOGON_COUNT = new QName("logonCount")
    val LOGON_MINUTES = new QName("logonMinutes")

    val ROW = new QName("row")
    val ROWSET = new QName("rowset")
    val CHARACTER_ID = new QName("characterID")
    val CHARACTER_NAME = new QName("characterName")
    val CORPORATION_ID = new QName("corporationID")
    val CORPORATION_NAME = new QName("corporationName")
    val ALLIANCE_ID = new QName("allianceID")
    val ALLIANCE_NAME = new QName("allianceName")
    val FACTION_ID = new QName("factionID")
    val FACTION_NAME = new QName("factionName")

    val ITEM_ID = new QName("itemID")
    val LOCATION_ID = new QName("locationID")
    val TYPE_ID = new QName("typeID")
    val QUANTITY = new QName("quantity")
    val FLAG = new QName("flag")
    val SINGLETON = new QName("singleton")
    val RAW_QUANTITY = new QName("rawQuantity")
    val NAME = new QName("name")

    val ERROR = new QName("error")
    val CODE = new QName("code")
  }
}
