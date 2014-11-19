package xmlparser.account

import models.eveapi.account.{Account, ApiKeyInfo}
import models.eveapi.eve.{Character, CharacterID, Corporation, CorporationID}
import models.eveats.ApiKeyID
import org.joda.time.{DateTime, DateTimeZone}
import org.specs2.mutable._

class ApiKeyInfoParserSpec extends Specification {

  val validXML =
    """
      |<eveapi version="2">
      |  <currentTime>2014-11-08 11:14:24</currentTime>
      |  <result>
      |    <key accessMask="268435455" type="Account" expires="">
      |      <rowset name="characters" key="characterID" columns="characterID,characterName,corporationID,corporationName,allianceID,allianceName,factionID,factionName">
      |        <row characterID="220728847" characterName="NP Complete" corporationID="98341385" corporationName="The Zeta Project" allianceID="0" allianceName="" factionID="0" factionName="" />
      |        <row characterID="1855361391" characterName="Dark Turing" corporationID="1000045" corporationName="Science and Trade Institute" allianceID="0" allianceName="" factionID="0" factionName="" />
      |      </rowset>
      |    </key>
      |  </result>
      |  <cachedUntil>2014-11-08 11:19:23</cachedUntil>
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
      val apiKeyID = ApiKeyID(1)
      val cachedUntil = new DateTime(2014, 11, 8, 11, 19, 23, DateTimeZone.UTC)
      
      val keyInfo = ApiKeyInfo(apiKeyID, 268435455, Account, None, cachedUntil)
      
      val characters = Set(
        Character(CharacterID(220728847), "NP Complete", CorporationID(98341385), cachedUntil),
        Character(CharacterID(1855361391), "Dark Turing", CorporationID(1000045), cachedUntil)
      )
      
      val corporations = Set(
        Corporation(CorporationID(98341385), "The Zeta Project"),
        Corporation(CorporationID(1000045), "Science and Trade Institute")
      )
      
      ApiKeyInfoParser(validXML, apiKeyID) must beASuccessfulTry((keyInfo, characters, corporations))
    }

    "throw InvalidXML exception on wrong XML data" in {
      ApiKeyInfoParser(invalidXML, ApiKeyID(1)) must beAFailedTry(new xmlparser.InvalidXML("Invalid XML data"))
    }

    "throw EveAPI exception on API error" in {
      ApiKeyInfoParser(xmlError, ApiKeyID(1)) must beAFailedTry(new xmlparser.EveApiError(203, "Authentication failure."))
    }
  }
}
