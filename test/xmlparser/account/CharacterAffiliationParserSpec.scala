package xmlparser.account

import models.eveapi.account.{Account, ApiKeyInfo}
import models.eveapi.eve.{Character, CharacterID, Corporation, CorporationID}
import models.eveats.ApiKeyID
import org.joda.time.{DateTime, DateTimeZone}
import org.specs2.mutable._
import xmlparser.eve.CharacterAffiliationParser

class CharacterAffiliationParserSpec extends Specification {

  val validXML =
    """
      |<eveapi version="2">
      | <currentTime>2014-06-06 13:06:41</currentTime>
      | <result>
      |   <rowset name="characters" key="characterID" columns="characterName,characterID,corporationName,corporationID,allianceName,allianceID,factionName,factionID">
      |     <row characterID="92168909" characterName="CCP FoxFour" corporationID="109299958" corporationName="C C P" allianceID="434243723" allianceName="C C P Alliance" factionID="0" factionName=""/>
      |     <row characterID="1188435724" characterName="CCP Prism X" corporationID="109299958" corporationName="C C P" allianceID="434243723" allianceName="C C P Alliance" factionID="0" factionName=""/>
      |     <row characterID="196379789" characterName="Chribba" corporationID="1164409536" corporationName="Otherworld Enterprises" allianceID="159826257" allianceName="Otherworld Empire" factionID="0" factionName=""/>
      |   </rowset>
      | </result>
      | <cachedUntil>2014-06-06 14:06:41</cachedUntil>
      |</eveapi>
    """.stripMargin

  val invalidXML =
    """
      |<eveapi version="2">
      |</eveapi>
    """.stripMargin

  val xmlError =
    """
      |<eveapi version="2">
      |  <currentTime>2014-04-25 16:03:51</currentTime>
      |    <error code="203">Authentication failure.</error>
      |  <cachedUntil>2014-04-26 16:03:51</cachedUntil>
      |</eveapi>
    """.stripMargin

  "ApiKeyInfoParser" should {
    "parse apiKeyInfo XML data" in {
      val cachedUntil = new DateTime(2014, 6, 6, 14, 6, 41, DateTimeZone.UTC)

      val characters = Set(
        Character(CharacterID(92168909), "CCP FoxFour", CorporationID(109299958), cachedUntil),
        Character(CharacterID(1188435724), "CCP Prism X", CorporationID(109299958), cachedUntil),
        Character(CharacterID(196379789), "Chribba", CorporationID(1164409536), cachedUntil)
      )

      val corporations = Set(
        Corporation(CorporationID(109299958), "C C P"),
        Corporation(CorporationID(1164409536), "Otherworld Enterprises")
      )

      CharacterAffiliationParser(validXML) must beASuccessfulTry((characters, corporations))
    }

    "throw InvalidXML exception on wrong XML data" in {
      ApiKeyInfoParser(invalidXML, ApiKeyID(1)) must beAFailedTry(new xmlparser.InvalidXML("Invalid XML data"))
    }

    "throw EveAPI exception on API error" in {
      ApiKeyInfoParser(xmlError, ApiKeyID(1)) must beAFailedTry(new xmlparser.EveApiError(203, "Authentication failure."))
    }
  }
}
