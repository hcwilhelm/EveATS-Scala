package xmlparser.character

import javax.xml.stream.XMLEventReader
import javax.xml.stream.XMLStreamConstants._

import models.eveapi.common.{RawQuantityBase, AssetItem, AssetItemID, AssetListID}
import models.evedump.{ItemID, TypeID}
import org.joda.time.DateTime
import xmlparser._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.Try

/**
* XML Example Success
*
* <?xml version='1.0' encoding='UTF-8'?>
* <eveapi version="2">
*   <currentTime>2014-05-26 23:17:46</currentTime>
*   <result>
*     <rowset name="assets" key="itemID" columns="itemID,locationID,typeID,quantity,flag,singleton">
*       <row itemID="150354641" locationID="30000380" typeID="11019" quantity="1" flag="0" singleton="1">
*         <rowset name="contents" key="itemID" columns="itemID,typeID,quantity,flag,singleton">
*           <row itemID="150354709" typeID="16275" quantity="200000" flag="5" singleton="0" />
*           <row itemID="150354710" typeID="16272" quantity="150000" flag="5" singleton="0" />
*           <row itemID="150354711" typeID="16273" quantity="150000" flag="5" singleton="0" />
*           <row itemID="150354712" typeID="24597" quantity="1000" flag="5" singleton="0" />
*           <row itemID="150354713" typeID="24596" quantity="1000" flag="5" singleton="0" />
*           <row itemID="150354714" typeID="24595" quantity="1000" flag="5" singleton="0" />
*           <row itemID="150354715" typeID="24594" quantity="1000" flag="5" singleton="0" />
*           <row itemID="150354716" typeID="24593" quantity="1000" flag="5" singleton="0" />
*           <row itemID="150354717" typeID="24592" quantity="1000" flag="5" singleton="0" />
*           <row itemID="150354718" typeID="16274" quantity="450000" flag="5" singleton="0" />
*           <row itemID="150354719" typeID="9848" quantity="1000" flag="5" singleton="0" />
*           <row itemID="150354720" typeID="9832" quantity="8000" flag="5" singleton="0" />
*           <row itemID="150354721" typeID="3689" quantity="5000" flag="5" singleton="0" />
*           <row itemID="150354722" typeID="3683" quantity="25000" flag="5" singleton="0" />
*           <row itemID="150354723" typeID="44" quantity="4000" flag="5" singleton="0" />
*         </rowset>
*       </row>
*       <row itemID="1094995162" locationID="60012667" typeID="11172" quantity="1" flag="4" singleton="1" rawQuantity="-1">
*         <rowset name="contents" key="itemID" columns="itemID,typeID,quantity,flag,singleton">
*           <row itemID="268146868" typeID="2456" quantity="1" flag="87" singleton="0"/>
*           <row itemID="454106425" typeID="28756" quantity="1" flag="28" singleton="1" rawQuantity="-1"/>
*           <row itemID="945410335" typeID="11578" quantity="1" flag="27" singleton="1" rawQuantity="-1"/>
*           <row itemID="1508974901" typeID="440" quantity="1" flag="19" singleton="1" rawQuantity="-1"/>
*           <row itemID="1001926945154" typeID="1319" quantity="1" flag="12" singleton="1" rawQuantity="-1"/>
*           <row itemID="1001926945428" typeID="1319" quantity="1" flag="13" singleton="1" rawQuantity="-1"/>
*           <row itemID="1001926990142" typeID="2032" quantity="1" flag="20" singleton="1" rawQuantity="-1"/>
*           <row itemID="1001926990464" typeID="2032" quantity="1" flag="21" singleton="1" rawQuantity="-1"/>
*           <row itemID="1010798440381" typeID="22177" quantity="1" flag="22" singleton="1" rawQuantity="-1"/>
*           <row itemID="1010798836679" typeID="22175" quantity="1" flag="23" singleton="1" rawQuantity="-1"/>
*           <row itemID="1010798854323" typeID="31213" quantity="1" flag="93" singleton="1" rawQuantity="-1"/>
*           <row itemID="1010798855870" typeID="31213" quantity="1" flag="92" singleton="1" rawQuantity="-1"/>
*           <row itemID="1012891238434" typeID="30488" quantity="1" flag="5" singleton="0"/>
*           <row itemID="1012891238439" typeID="30488" quantity="1" flag="5" singleton="0"/>
*           <row itemID="1012891238460" typeID="30488" quantity="1" flag="5" singleton="0"/>
*           <row itemID="1012891238496" typeID="30488" quantity="1" flag="5" singleton="0"/>
*           <row itemID="1012891238510" typeID="30488" quantity="1" flag="5" singleton="0"/>
*           <row itemID="1012891238519" typeID="30488" quantity="1" flag="5" singleton="0"/>
*           <row itemID="1012891238529" typeID="30488" quantity="1" flag="5" singleton="0"/>
*           <row itemID="1012891238541" typeID="30488" quantity="1" flag="5" singleton="0"/>
*         </rowset>
*       </row>
*       <row itemID="150212062" locationID="60001078" typeID="944" quantity="1" flag="4" singleton="1" />
*       <row itemID="150212063" locationID="60001078" typeID="597" quantity="1" flag="4" singleton="0" />
*     </rowset>
*   </result>
*   <cachedUntil>2014-05-27 05:17:46</cachedUntil>
* </eveapi>
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


class AssetListParser(val reader: XMLEventReader) {

  var itemStack: List[AssetItemID] = List()
  var currentItemID: AssetItemID = _
  val items: mutable.ListBuffer[AssetListID => AssetItem] = new ListBuffer()

  @throws[XMLParserException]("If EveAPI returns error or we have the wrong XML data")
  def parse: (Seq[AssetListID => AssetItem], DateTime) = {
    while (reader.hasNext) {
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
           * Parse Row
           */
          if (element.getName == QNames.ROW) {
            val id = AssetItemID(element.getAttributeByName(QNames.ITEM_ID).getValue.toLong)
            val locationID = Try {ItemID(element.getAttributeByName(QNames.LOCATION_ID).getValue.toLong)}.toOption
            val typeID = TypeID(element.getAttributeByName(QNames.TYPE_ID).getValue.toLong)
            val quantity = element.getAttributeByName(QNames.QUANTITY).getValue.toInt
            val flag = element.getAttributeByName(QNames.FLAG).getValue.toInt

            val singleton = element.getAttributeByName(QNames.SINGLETON).getValue match {
              case "1" => true
              case "0" => false
            }

            val rawQuantity = Try {RawQuantityBase(element.getAttributeByName(QNames.RAW_QUANTITY).getValue.toInt)}.toOption

            val parentID = itemStack.headOption

            currentItemID = id
            items += (AssetItem(id, _: AssetListID, parentID, locationID, typeID, quantity, flag, singleton, rawQuantity))
          }

          /**
           * Parse RowSet with name -> contents
           * and push itemID onto the stack
           */
          if(element.getName == QNames.ROWSET && element.getAttributeByName(QNames.NAME).getValue == "contents") {
            itemStack = currentItemID::itemStack
          }

          /**
           * Parse cached until and return
           */
          if(element.getName == QNames.CACHED_UNTIL) {
            val cachedUntil = dateTimeFormatter.parseDateTime(reader.nextEvent().asCharacters().getData)

            reader.close()
            return (items.toList, cachedUntil)
          }

        case END_ELEMENT =>
          val element = event.asEndElement()

          /**
           * Closing RowSet pop element form stack
           */
          if (element.getName == QNames.ROWSET) {
            itemStack = if (itemStack.nonEmpty) itemStack.tail else itemStack
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

object AssetListParser {
  def apply(xml: String): Try[(Seq[AssetListID => AssetItem], DateTime)] = {
    Try(new AssetListParser(EventReaderFactory(xml)).parse)
  }
}