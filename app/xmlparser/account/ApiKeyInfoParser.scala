package xmlparser.account

import javax.xml.stream.XMLEventReader
import javax.xml.stream.XMLStreamConstants._

import models.eveapi.account.{KeyType, ApiKeyInfo}
import models.eveapi.eve._
import models.eveats.ApiKeyID
import org.joda.time.DateTime
import xmlparser._

import scala.util.Try
import scala.collection.mutable

/**
 *
 * Example XML success:
 *
 * <?xml version='1.0' encoding='UTF-8'?>
 * <eveapi version="2">
 * <currentTime>2014-11-08 10:29:32</currentTime>
 *  <result>
 *    <key accessMask="268435455" type="Account" expires="">
 *      <rowset name="characters" key="characterID" columns="characterID,characterName,corporationID,corporationName,allianceID,allianceName,factionID,factionName">
 *        <row characterID="220728847" characterName="NP Complete" corporationID="98341385" corporationName="The Zeta Project" allianceID="0" allianceName="" factionID="0" factionName=""/>
 *        <row characterID="1855361391" characterName="Dark Turing" corporationID="1000045" corporationName="Science and Trade Institute" allianceID="0" allianceName="" factionID="0" factionName=""/>
 *      </rowset>
 *    </key>
 *  </result>
 * <cachedUntil>2014-11-08 10:34:32</cachedUntil>
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

private class ApiKeyInfoParser(val reader: XMLEventReader, val keyID: ApiKeyID) {

  var partialApiKeyInfo: DateTime => ApiKeyInfo = _

  val characters: mutable.ListBuffer[DateTime => Character] = new mutable.ListBuffer()
  val corporations: mutable.ListBuffer[Corporation] = new mutable.ListBuffer()

  @throws[XMLParserException]("If EveAPI returns error or we have the wrong XML data")
  def parse: (ApiKeyInfo, Set[Character], Set[Corporation]) = {
    while(reader.hasNext) {
      val event = reader.nextEvent()

      event.getEventType match {
        case START_ELEMENT =>
          val element = event.asStartElement()

          /**
           * Parse Error => return
           */
          if (element.getName == QNames.ERROR) {
            val code = element.getAttributeByName(QNames.CODE).getValue.toInt
            val message = reader.nextEvent().asCharacters().getData

            reader.close()
            throw new EveApiError(code, message)
          }

          /**
           * Parse partial key info
           */
          if(element.getName == QNames.KEY) {
            val accessMask = element.getAttributeByName(QNames.ACCESS_MASK).getValue.toInt
            val keyType = KeyType(element.getAttributeByName(QNames.TYPE).getValue)
            val expires = element.getAttributeByName(QNames.EXPIRES).getValue match {
              case ""   => None
              case date => Some(dateTimeFormatter.parseDateTime(date))
            }

            partialApiKeyInfo = ApiKeyInfo(keyID, accessMask, keyType, expires, _: DateTime)
          }

          if(element.getName == QNames.ROW) {
            val characterID = CharacterID(element.getAttributeByName(QNames.CHARACTER_ID).getValue.toLong)
            val characterName = element.getAttributeByName(QNames.CHARACTER_NAME).getValue

            val corporationID = CorporationID(element.getAttributeByName(QNames.CORPORATION_ID).getValue.toLong)
            val corporationName = element.getAttributeByName(QNames.CORPORATION_NAME).getValue


            characters += (Character(characterID, characterName, corporationID, _: DateTime))
            corporations += Corporation(corporationID, corporationName)
          }

          /**
           * Parse cached until and finalize partialApiKeyInfo => return
           */
          if(element.getName == QNames.CACHED_UNTIL) {
            val cachedUntil = dateTimeFormatter.parseDateTime(reader.nextEvent().asCharacters().getData)

            reader.close()
            return (partialApiKeyInfo(cachedUntil), characters.map(_.apply(cachedUntil)).toSet, corporations.toSet)
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

object ApiKeyInfoParser {
  def apply(xml: String, keyId: ApiKeyID): Try[(ApiKeyInfo, Set[Character], Set[Corporation])] = {
    Try(new ApiKeyInfoParser(EventReaderFactory(xml), keyId).parse)
  }
}