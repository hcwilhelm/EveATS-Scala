package xmlparser.eve

import javax.xml.stream.XMLEventReader
import javax.xml.stream.XMLStreamConstants._

import models.eveapi.eve._
import org.joda.time.DateTime
import xmlparser._

import scala.collection.mutable
import scala.util.Try

/**
 * Example XML Success
 *
 * <eveapi version="2">
 *  <currentTime>2014-06-06 13:06:41</currentTime>
 *    <result>
 *      <rowset name="characters" key="characterID" columns="characterName,characterID,corporationName,corporationID,allianceName,allianceID,factionName,factionID">
 *        <row characterID="92168909" characterName="CCP FoxFour" corporationID="109299958" corporationName="C C P" allianceID="434243723" allianceName="C C P Alliance" factionID="0" factionName=""/>
 *        <row characterID="1188435724" characterName="CCP Prism X" corporationID="109299958" corporationName="C C P" allianceID="434243723" allianceName="C C P Alliance" factionID="0" factionName=""/>
 *        <row characterID="196379789" characterName="Chribba" corporationID="1164409536" corporationName="Otherworld Enterprises" allianceID="159826257" allianceName="Otherworld Empire" factionID="0" factionName=""/>
 *      </rowset>
 *    </result>
 *  <cachedUntil>2014-06-06 14:06:41</cachedUntil>
 * </eveapi>
 *
 * Example XML Error:
 *
 * <eveapi version="2">
 *   <currentTime>2014-04-25 16:03:51</currentTime>
 *     <error code="203">Authentication failure.</error>
 *   <cachedUntil>2014-04-26 16:03:51</cachedUntil>
 * </eveapi>
 */

private class CharacterAffiliationParser(val reader: XMLEventReader) {

  val characters: mutable.ListBuffer[DateTime => Character] = new mutable.ListBuffer()
  val corporations: mutable.ListBuffer[Corporation] = new mutable.ListBuffer()

  @throws[XMLParserException]("If EveAPI returns error or we have the wrong XML data")
  def parse: (Set[Character], Set[Corporation]) = {
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
           * Parse row
           */
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
            return (characters.map(_.apply(cachedUntil)).toSet, corporations.toSet)
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

object CharacterAffiliationParser {
  def apply(xml: String): Try[(Set[Character], Set[Corporation])] = {
    Try(new CharacterAffiliationParser(EventReaderFactory(xml)).parse)
  }
}