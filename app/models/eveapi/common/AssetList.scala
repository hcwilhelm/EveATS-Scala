package models.eveapi.common

import models.core.TypesafeID._
import models.core.TypesafeID.driver.simple._
import models.eveapi.eve.AffiliationID
import models.evedump.{ItemID, TypeID}

import org.joda.time.DateTime
import play.api.libs.json.{JsResult, JsValue, Format, Json}

import scala.slick.lifted.ForeignKeyQuery
import scala.slick.model.ForeignKeyAction.Cascade

/**
 * AssetListID
 *
 * @param untypedID
 */
case class AssetListID(untypedID: Long) extends AnyVal with TypedID
object AssetListID extends TypedIDCompanion[AssetListID]

/**
 * AssetList
 *
 * @tparam REF
 */
trait AssetList[REF <: AffiliationID] extends WithOptionalID[AssetListID] {
  def id: Option[AssetListID]
  def affiliationID: REF
  def createdAt: DateTime
  def cachedUntil: DateTime
}

/**
 * AssetListTable
 *
 * @param tag
 * @param schemaName
 * @param tableName
 * @param idMapping
 * @param refMapping
 * @tparam REF
 * @tparam Entity
 */
abstract class AssetListTable[REF <: AffiliationID, Entity <: AssetList[REF]](tag: Tag, schemaName: Option[String], tableName: String)(implicit val idMapping: BaseColumnType[AssetListID], val refMapping: BaseColumnType[REF])
extends BaseTable[Entity](tag, schemaName, tableName) {
  def this(tag: Tag, tableName: String)(implicit idMapping: BaseColumnType[AssetListID], refMapping: BaseColumnType[REF]) = this(tag, None, tableName)

  def id = column[AssetListID]("id", O.AutoInc)
  def affiliationID = column[REF]("affiliation_id")
  def createdAt = column[DateTime]("created_at")
  def cachedUntil = column[DateTime]("cached_until")

  def pk = primaryKey(tableName + "_id_affiliation_id_pk", (id, affiliationID))
  def idIDX = index(tableName + "_id_idx", id, unique = true)
}

/**
 * AssetListRepository
 *
 * @param query
 * @param idMapping
 * @param refMapping
 * @tparam REF
 * @tparam Entity
 * @tparam Table
 */
class AssetListRepository[REF <: AffiliationID, Entity <: AssetList[REF], Table <: AssetListTable[REF, Entity]](val query: TableQuery[Table])(implicit val idMapping: BaseColumnType[AssetListID], val refMapping: BaseColumnType[REF]) {
  import CustomTypeMappers._

  def findAll(implicit s: Session): Seq[Entity] =
    query.list

  def findAll(ref: REF)(implicit s: Session): Seq[Entity] =
    query.filter(_.affiliationID === ref).list

  def find(id: AssetListID, refID: REF)(implicit s: Session): Option[Entity] =
    query.filter(_.id === id).filter(_.affiliationID === refID).firstOption

  def find(ids: (AssetListID, REF)*)(implicit s: Session): Seq[Entity] =
    query.filter(_.id inSet ids.map(_._1)).filter(_.affiliationID inSet ids.map(_._2)).list

  def exists(id: AssetListID, refID: REF)(implicit s: Session): Boolean =
    query.filter(_.id === id).filter(_.affiliationID === refID).exists.run

  def insert(elem: Entity)(implicit s: Session): AssetListID =
    (query returning query.map(_.id)) += elem

  def insert(elem: Entity*)(implicit s: Session): Seq[AssetListID] =
    (query returning query.map(_.id)) ++= elem

  def update(elem: Entity)(implicit s: Session): Option[Int] =
    elem.id map (id => query.filter(_.id === id).filter(_.affiliationID === elem.affiliationID).update(elem))

  def insertOrUpdate(elem: Entity)(implicit s: Session): Option[AssetListID] =
    (query returning query.map(_.id)) insertOrUpdate(elem)

  def delete(id: AssetListID, refID: REF)(implicit s: Session): Int =
    query.filter(_.id === id).filter(_.affiliationID === refID).delete

  def delete(ids: (AssetListID, REF)*)(implicit s: Session): Int =
    query.filter(_.id inSet ids.map(_._1)).filter(_.affiliationID inSet ids.map(_._2)).delete

  private val latestQuery = query.groupBy( table => (table.id, table.affiliationID)) map {
    case (pk, entity) => pk -> entity.map(_.createdAt).max
  }

  def findLatest(ref: REF)(implicit s: Session): Option[Entity] =
    (for {
      assetList <- query
      ((id, ref), date) <- latestQuery if assetList.id === id && assetList.affiliationID === ref && assetList.createdAt === date
    } yield assetList).firstOption
}

/**
 * AssetItemID
 *
 * @param untypedID
 */
case class AssetItemID(untypedID: Long) extends AnyVal with TypedID
object AssetItemID extends TypedIDCompanion[AssetItemID]

sealed trait RawQuantityBase
case class RawQuantity(quantity: Int) extends RawQuantityBase
case object BluePrintOriginal extends RawQuantityBase
case object BluePrintCopy extends RawQuantityBase

object RawQuantityBase {
  def apply(value: Int) = value match {
    case -1 => BluePrintOriginal
    case -2 => BluePrintCopy
    case  _ => RawQuantity(value)
  }

  def unapply(value: RawQuantityBase) = value match {
    case BluePrintOriginal => -1
    case BluePrintCopy => -2
    case rawQuantity: RawQuantity => rawQuantity.quantity
  }

  implicit val jsonFormat = new Format[RawQuantityBase] {
    override def reads(json: JsValue): JsResult[RawQuantityBase] = json.validate[Int].map(RawQuantityBase(_))
    override def writes(o: RawQuantityBase): JsValue = Json.toJson(RawQuantityBase.unapply(o))
  }
}

/**
 * AssetItem
 *
 * @param id
 * @param assetListID
 * @param parentID
 * @param locationID
 * @param typeID
 * @param quantity
 * @param flag
 * @param singleton
 * @param rawQuantity
 */
case class AssetItem(
  id: AssetItemID,
  assetListID: AssetListID,
  parentID: Option[AssetItemID],
  locationID: Option[ItemID],
  typeID: TypeID,
  quantity: Int,
  flag: Int,
  singleton: Boolean,
  rawQuantity: Option[RawQuantityBase])

object AssetItem { implicit val jsonFormat = Json.format[AssetItem] }

/**
 * AssetItemTable
 *
 * @param tag
 * @param schemaName
 * @param tableName
 * @param idMapping
 */
abstract class AssetItemTable[REF <: AffiliationID, Entity <: AssetList[REF], TABLE <: AssetListTable[REF, Entity]](tag: Tag, schemaName: Option[String], tableName: String)(val assetListQuery: TableQuery[TABLE])(implicit val idMapping: BaseColumnType[AssetItemID])
extends BaseTable[AssetItem](tag, schemaName, tableName) {
  def this(tag: Tag, tableName: String)(assetListQuery: TableQuery[TABLE])(implicit idMapping: BaseColumnType[AssetItemID]) = this(tag, None, tableName)(assetListQuery)

  implicit val rawQuantityColumnMapper = MappedColumnType.base[RawQuantityBase, Int](RawQuantityBase.unapply(_), RawQuantity.apply(_))

  def id = column[AssetItemID]("id")
  def assetListID = column[AssetListID]("asset_list_id")
  def parentID = column[Option[AssetItemID]]("parent_id")
  def locationID = column[Option[ItemID]]("location_id")
  def typeID = column[TypeID]("type_id")
  def quantity = column[Int]("quantity")
  def flag = column[Int]("flag")
  def singleton = column[Boolean]("singleton")
  def rawQuantity = column[Option[RawQuantityBase]]("raw_quantity")

  def pk = primaryKey(tableName + "_id_asset_list_pk", (id, assetListID))
  def assetList = foreignKey(tableName + "_asset_list_fk", assetListID, assetListQuery)(_.id, onUpdate = Cascade, onDelete = Cascade)

  def * = (
    id,
    assetListID,
    parentID,
    locationID,
    typeID,
    quantity,
    flag,
    singleton,
    rawQuantity) <> ((AssetItem.apply _).tupled, AssetItem.unapply)
}

/**
 * AssetItemRepository
 *
 * @param query
 * @param idMapping
 * @tparam Table
 */
class AssetItemRepository[Table <: AssetItemTable[_, _, _]](val query: TableQuery[Table])(implicit val idMapping: BaseColumnType[AssetListID]) {
  import CustomTypeMappers._

  def findAll(implicit s: Session): Seq[AssetItem] =
    query.list

  def findAll(ref: AssetListID)(implicit s: Session): Seq[AssetItem] =
    query.filter(_.assetListID === ref).list

  def find(id: AssetItemID, refID: AssetListID)(implicit s: Session): Option[AssetItem] =
    query.filter(_.id === id).filter(_.assetListID === refID).firstOption

  def find(ids: (AssetItemID, AssetListID)*)(implicit s: Session): Seq[AssetItem] =
    query.filter(_.id inSet ids.map(_._1)).filter(_.assetListID inSet ids.map(_._2)).list

  def exists(id: AssetItemID, refID: AssetListID)(implicit s: Session): Boolean =
    query.filter(_.id === id).filter(_.assetListID === refID).exists.run

  def insert(elem: AssetItem)(implicit s: Session): AssetItemID =
    (query returning query.map(_.id)) += elem

  def insert(elem: AssetItem*)(implicit s: Session): Seq[AssetItemID] =
    (query returning query.map(_.id)) ++= elem

  def update(elem: AssetItem)(implicit s: Session): Int =
    query.filter(_.id === elem.id).filter(_.assetListID === elem.assetListID).update(elem)

  def insertOrUpdate(elem: AssetItem)(implicit s: Session): Option[AssetItemID] =
    (query returning query.map(_.id)) insertOrUpdate(elem)

  def delete(id: AssetItemID, refID: AssetListID)(implicit s: Session): Int =
    query.filter(_.id === id).filter(_.assetListID === refID).delete

  def delete(ids: (AssetItemID, AssetListID)*)(implicit s: Session): Int =
    query.filter(_.id inSet ids.map(_._1)).filter(_.assetListID inSet ids.map(_._2)).delete
}

/**
 * AssetTree
 *
 */
sealed trait AssetTree
case class AssetTreeNode(item: AssetItem, childs: Set[AssetTreeNode]) extends AssetTree

object AssetTreeNode { implicit val jsonFormat = Json.format[AssetTreeNode] }

object AssetTree {
  def apply(root: AssetItem, data: Set[AssetItem]): AssetTreeNode = {
    val childs = data.filter(_.parentID == Some(root.id))
    AssetTreeNode(root, childs.map(AssetTree(_, data -- childs)))
  }
}


