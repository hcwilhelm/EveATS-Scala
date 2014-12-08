package models.core

trait Tables extends TypeMappers { self: HasJdbcDriver with Identifiers =>
  import driver.simple._

  abstract class BaseTable[Entity](tag: Tag, schemaName: Option[String], tableName: String) extends Table[Entity](tag, schemaName, tableName) with CustomTypeMappers {
    def this(tag: Tag, tableName: String) = this(tag, None, tableName)
  }

  abstract class OptionalIDTable[ID <: TypedID, Entity <: WithOptionalID[ID]](tag: Tag, schemaName: Option[String], tableName: String)(implicit val mapping: BaseColumnType[ID])
  extends BaseTable[Entity](tag, schemaName, tableName) {
    def this(tag: Tag, tableName: String)(implicit mapping: BaseColumnType[ID]) = this(tag, None, tableName)

    def id = column[ID]("id", O.PrimaryKey, O.AutoInc)
  }

  abstract class IDTable[ID <: TypedID, Entity <: WithID[ID]](tag: Tag, schemaName: Option[String], tableName: String)(implicit val mapping: BaseColumnType[ID])
  extends BaseTable[Entity](tag, schemaName, tableName) {
    def this(tag: Tag, tableName: String)(implicit mapping: BaseColumnType[ID]) = this(tag, None, tableName)

    def id = column[ID]("id", O.PrimaryKey)
  }
}
