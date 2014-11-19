package xmlparser.account

import models.eveapi.account.AccountStatus
import models.eveats.ApiKeyID
import org.joda.time.{DateTimeZone, Duration, DateTime}
import org.specs2.mutable._

class AccountStatusParserSpec extends Specification {

  val validXML =
    """
      |<eveapi version="2">
      |  <currentTime>2010-10-05 20:28:55</currentTime>
      |  <result>
      |    <paidUntil>2011-01-01 00:00:00</paidUntil>
      |    <createDate>2004-01-01 00:00:00</createDate>
      |    <logonCount>9999</logonCount>
      |    <logonMinutes>9999</logonMinutes>
      |  </result>
      |  <cachedUntil>2010-10-05 20:33:55</cachedUntil>
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

  "AccountStatusParser" should {
    "parse account status XML data" in {
      val accountStatus = AccountStatus(ApiKeyID(1), new DateTime(2011, 1, 1, 0, 0, 0, DateTimeZone.UTC), new DateTime(2004, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC), 9999, new Duration(9999 * 60 * 1000), new DateTime(2010, 10, 5, 20, 33, 55, DateTimeZone.UTC))
      AccountStatusParser(validXML, ApiKeyID(1)) must beASuccessfulTry(accountStatus)
    }

    "throw InvalidXML exception on wrong XML data" in {
      AccountStatusParser(invalidXML, ApiKeyID(1)) must beAFailedTry(new xmlparser.InvalidXML("Invalid XML data"))
    }

    "throw EveAPI exception on API error" in {
      AccountStatusParser(xmlError, ApiKeyID(1)) must beAFailedTry(new xmlparser.EveApiError(203, "Authentication failure."))
    }
  }
}
