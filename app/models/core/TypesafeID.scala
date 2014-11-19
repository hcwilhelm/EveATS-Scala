package models.core

import play.api.db.slick.Config
import scala.slick.driver.JdbcDriver

trait HasJdbcDriver {
  val driver: JdbcDriver
}

trait TypesafeIDCore extends HasJdbcDriver {
  override lazy val driver: JdbcDriver = Config.driver
}

object TypesafeID extends TypesafeIDCore with Identifiers with Tables with Repositories
