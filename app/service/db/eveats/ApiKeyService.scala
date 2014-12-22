package service.db.eveats

import models.eveats._
import play.api.Play.current
import play.api.db.slick.DB
import service.db.dbOperationsExecutionContext

import scala.concurrent._

/**
 * ApiKeyService
 *
 */
object ApiKeyService {

  def find(apiKeyID: ApiKeyID): Future[Option[ApiKey]] = Future {
    DB.withSession { implicit session =>
      ApiKeyTable.find(apiKeyID)
    }
  }

  def insert(apiKey: ApiKey): Future[ApiKeyID] = Future {
      DB.withSession { implicit session =>
        ApiKeyTable.insert(apiKey)
      }
  }

  def update(apiKey: ApiKey): Future[Int] = Future {
    DB.withSession { implicit session =>
      ApiKeyTable.update(apiKey)
    }
  }

  def delete(apiKeyID: ApiKeyID): Future[Int] = Future {
    DB.withSession { implicit session =>
      ApiKeyTable.delete(apiKeyID)
    }
  }
}
