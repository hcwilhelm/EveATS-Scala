package actors.emdr

import java.util.zip.Inflater

import akka.actor._
import models.emdr
import models.emdr.{OrderID, OrderTable}
import models.emdr.json.{Message, Order}
import models.evedump.{ItemID, TypeID, RegionID}
import org.zeromq.{ZMQException, ZMQ}
import org.zeromq.ZMQ.Context
import play.api.libs.json.{JsSuccess, Json}
import play.libs.Akka
import play.api.db.slick.DB
import play.api.Play.current

import scala.util.control.Breaks


class EmdrListener extends Actor {

  //ZeroMQExtension(Akka.system()).newSubSocket(Connect("tcp://relay-eu-germany-2.eve-emdr.com:8050"), Listener(self), SubscribeAll)

  override def receive = {
    case _ => println("Unknown msg")
  }
}

object EmdrListener {
  val listener = Akka.system().actorOf(Props[EmdrListener], "EmdrListener")


}

class EMDRService(ctx: Context) extends Runnable {

  val subscriber = ctx.socket(ZMQ.SUB)
  subscriber.connect("tcp://relay-us-central-1.eve-emdr.com:8050")
  subscriber.subscribe(Array.empty)

  val loop = new Breaks
  var currentOrder: Order = _

  override def run() = {

    loop.breakable {
      while (!Thread.currentThread().isInterrupted) {
        try {
          val msg = subscriber.recv()
          val buffer = new Array[Byte](msg.length * 8)

          val decompress = new Inflater()
          decompress.setInput(msg)

          Json.parse(buffer.take(decompress.inflate(buffer))).validate[Message] match {
            case JsSuccess(v: Order, _) =>
              currentOrder = v
              println(writeToDB(v))
            case _ => ()
          }

        } catch {
          case ex: ZMQException => loop.break()
          case ex: Exception =>
            println(ex.getMessage)
            println()
            println(currentOrder)
        }
      }
    }

    subscriber.close()
  }

  def writeToDB(order: Order): Seq[OrderID] = {

    val orders = order.rowsets flatMap { rowset =>
      rowset.rows map { row =>
        emdr.Order(
          None,
          rowset.generatedAt,
          rowset.regionID map RegionID.apply,
          TypeID(rowset.typeID),
          row.price,
          row.volRemaining,
          row.range,
          row.orderID,
          row.volEntered,
          row.minVolume,
          row.bid,
          row.issueDate,
          row.duration,
          ItemID(row.stationID),
          row.solarSystemID map ItemID.apply
        )
      }
    }

    DB.withSession { implicit session =>
      OrderTable.insert(orders: _*)
    }
  }
}

object EMDRService {
  val ctx = ZMQ.context(1);
  val service =  new Thread(new EMDRService(ctx))

  def start() =
    if (!service.isAlive)
      service.start()

  def stop() = {
    ctx.term()
    service.interrupt()
    service.join()
  }
}