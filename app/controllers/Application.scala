package controllers

import actors.emdr.{EMDRService, EmdrListener}
import models.emdr.{OrderTable, Order}
import models.emdr.json.Message
import models.eveapi.eve.{Character, CharacterID, CorporationID}
import models.eveats.{ApiKey, ApiKeyID}
import models.evedump.{MapDenormalize, MapDenormalizeTable, TypeID}
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.mvc._
import play.api.db.slick.DB
import play.api.Play.current
import models.core.TypesafeID.driver.simple._
import scala.language.postfixOps
import scala.slick.lifted

object Application extends Controller {

  def index = Action {

    val apiKey = ApiKey(ApiKeyID(1963281), "oMJ92FB2hFQgFaMG8wprX9B18lpdMvAazo7S7dX3fsc29zPBJn2PUTKmKT3052Gf")
    val char = Character(CharacterID(220728847), "NP Complete", CorporationID(98341385), DateTime.now())

    DB.withSession { implicit session =>
      val q = for {
        (id, rows) <- OrderTable.query.filter(_.typeID === TypeID(34)).filter(_.bid === false).sortBy(_.price.asc).take(120).groupBy(_.typeID)
      } yield (id, rows.map(_.price).avg)

      val q2: lifted.Query[OrderTable, OrderTable#TableElementType, Seq] = OrderTable.query.filter(_.typeID === TypeID(34)).filter(_.bid === false)

      val q3: lifted.Query[(OrderTable, MapDenormalizeTable), (Order, MapDenormalize), Seq] = for {
        order <- OrderTable.query if (order.typeID === TypeID(34)) && (order.bid === false)
        location <- MapDenormalizeTable.query if location.id === order.solarSystemID && location.itemName === "Jita"
      } yield (order, location)

      println(q2.run)
      println(q.run)
      q3.sortBy(_._1.price).map {
        case (order, location) => (order.price, order.volEntered, location.itemName)
      }.run map println
    }



    Ok(views.html.index("Your new application is ready."))
  }


}