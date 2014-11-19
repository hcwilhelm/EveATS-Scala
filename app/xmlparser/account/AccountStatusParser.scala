package xmlparser.account

import javax.xml.stream.XMLEventReader
import javax.xml.stream.XMLStreamConstants._

import models.eveapi.account.AccountStatus
import models.eveats.ApiKeyID
import org.joda.time.{DateTime, Duration}
import xmlparser._

import scala.util.Try

/**
 * Example XML :
 *
 * <eveapi version="2">
 *  <currentTime>2014-05-02 14:39:45</currentTime>
 *    <result>
 *     <paidUntil>2014-01-27 22:05:41</paidUntil>
 *     <createDate>2007-11-21 13:55:00</createDate>
 *     <logonCount>4410</logonCount>
 *     <logonMinutes>499970</logonMinutes>
 *    </result>
 *  <cachedUntil>2014-05-02 15:36:45</cachedUntil>
 * </eveapi>
 *
 *
 * Example XML Error:
 *
 * <eveapi version="2">
 *   <currentTime>2014-04-25 16:03:51</currentTime>
 *     <error code="203">Authentication failure.</error>
 *   <cachedUntil>2014-04-26 16:03:51</cachedUntil>
 * </eveapi>
 *
 */

private class AccountStatusParser(val reader: XMLEventReader, val keyID: ApiKeyID) {

  var paidUntil: DateTime = _
  var createDate: DateTime = _
  var logonCount: Int = _
  var logonDuration: Duration = _

  @throws[XMLParserException]("If EveAPI returns an error or we have the wrong XML data")
  def parse: AccountStatus = {
    while(reader.hasNext) {
      val event = reader.nextEvent()

      event.getEventType match {
        case START_ELEMENT =>
          val element = event.asStartElement()

          /**
           * Parse Error
           */
          if (element.getName == QNames.ERROR) {
            val code = element.getAttributeByName(QNames.CODE).getValue.toInt
            val message = reader.nextEvent().asCharacters().getData

            reader.close()
            throw new EveApiError(code, message)
          }

          /**
           * Parse Paid Until
           */
          if(element.getName == QNames.PAID_UNTIL) {
            paidUntil = dateTimeFormatter.parseDateTime(reader.nextEvent().asCharacters().getData)
          }

          /**
           * Parse createDate
           */
          if(element.getName == QNames.CREATE_DATE) {
            createDate = dateTimeFormatter.parseDateTime(reader.nextEvent().asCharacters().getData)
          }

          /**
           * Parse Logon count
           */
          if(element.getName == QNames.LOGON_COUNT) {
            logonCount = reader.nextEvent().asCharacters().getData.toInt
          }

          /**
           * Parse Logon Duration
           */
          if(element.getName == QNames.LOGON_MINUTES) {
            val minutes = reader.nextEvent().asCharacters().getData.toLong
            logonDuration = new Duration(minutes * 60 * 1000)
          }

          /**
           * Parse CachedUntil, parsing finished close reader and early return
           */
          if(element.getName == QNames.CACHED_UNTIL) {
            val cachedUntil = dateTimeFormatter.parseDateTime(reader.nextEvent().asCharacters().getData)

            reader.close()
            return AccountStatus(keyID, paidUntil, createDate, logonCount, logonDuration, cachedUntil)
          }

        case _ => Unit
      }
    }

    /**
     * Something went wrong ;( => return
     */
    reader.close()
    throw new InvalidXML("Invalid XML data")
  }
}

object AccountStatusParser {
  def apply(xml: String, keyID: ApiKeyID): Try[AccountStatus] =
    Try(new AccountStatusParser(EventReaderFactory(xml), keyID).parse)
}