package models.core

trait Repositories { self: HasJdbcDriver with Identifiers with Tables =>
  import driver.simple._

  class OptionalIDRepository[ID <: TypedID, Entity <: WithOptionalID[ID], Table <: OptionalIDTable[ID, Entity]](val query: TableQuery[Table])(implicit val mapping: BaseColumnType[ID]) {

    def findAll(implicit s: Session): Seq[Entity] =
      query.list

    def find(id: ID)(implicit s: Session): Option[Entity] =
      query.filter(_.id === id).firstOption

    def find(ids: ID*)(implicit s: Session): Seq[Entity] =
      query.filter(_.id inSet ids).list

    def exists(id: ID)(implicit s: Session): Boolean =
      query.filter(_.id === id).exists.run

    def insert(elem: Entity)(implicit s: Session): ID =
      (query returning query.map(_.id)) += elem

    def insert(elem: Entity*)(implicit s: Session): Seq[ID] =
      (query returning query.map(_.id)) ++= elem

    def update(elem: Entity)(implicit s: Session): Option[Int] =
      elem.id map (id => query.filter(_.id === id).update(elem))

    def insertOrUpdate(elem: Entity)(implicit s: Session): Option[ID] =
      (query returning query.map(_.id)).insertOrUpdate(elem)

    def insertOrUpdate(elem: Entity*)(implicit s: Session): Seq[Option[ID]] = {
      val q = query returning query.map(_.id)
      elem.map(q.insertOrUpdate)
    }

    def delete(id: ID)(implicit s: Session): Int =
      query.filter(_.id === id).delete

    def delete(ids: ID*)(implicit s: Session): Int =
      query.filter(_.id inSet ids).delete
  }

  class IDRepository[ID <: TypedID, Entity <: WithID[ID], Table <: IDTable[ID, Entity]](val query: TableQuery[Table])(implicit mapping: BaseColumnType[ID]) {

    def findAll(implicit s: Session): Seq[Entity] =
      query.list

    def find(id: ID)(implicit s: Session): Option[Entity] =
      query.filter(_.id === id).firstOption

    def find(ids: ID*)(implicit s: Session): Seq[Entity] =
      query.filter(_.id inSet ids).list

    def exists(id: ID)(implicit s: Session): Boolean =
      query.filter(_.id === id).exists.run

    def insert(elem: Entity)(implicit s: Session): ID =
      (query returning query.map(_.id)) += elem

    def insert(elem: Entity*)(implicit s: Session): Seq[ID] =
      (query returning query.map(_.id)) ++= elem

    def update(elem: Entity)(implicit s: Session): Int =
      query.filter(_.id === elem.id).update(elem)

    def insertOrUpdate(elem: Entity)(implicit s: Session): Option[ID] =
      (query returning query.map(_.id)).insertOrUpdate(elem)

    def insertOrUpdate(elem: Entity*)(implicit s: Session): Seq[Option[ID]] = {
      val q = query returning query.map(_.id)
      elem.map(q.insertOrUpdate)
    }

    def delete(id: ID)(implicit s: Session): Int =
      query.filter(_.id === id).delete

    def delete(ids: ID*)(implicit s: Session): Int =
      query.filter(_.id inSet ids).delete
  }

}
