package service.db.eveats

import models.eveats._
import play.api.Play.current
import play.api.db.slick.DB
import service.db.executionContext

import scala.concurrent._

/**
 * ApiKeyService
 *
 */
object ApiKeyService {

  def find(key: ApiKeyID): Future[Option[ApiKey]] = Future {
    DB.withSession { implicit session =>
      ApiKeyTable.find(key)
    }
  }

  def insert(key: ApiKey): Future[ApiKeyID] = Future {
      DB.withSession { implicit session =>
        ApiKeyTable.insert(key)
      }
  }
  
  def insertOrUpdate(key: ApiKey) = Future {
    DB.withSession { implicit session =>
      ApiKeyTable.insertOrUpdate(key)
    }
  }

  def update(apiKey: ApiKey): Future[Int] = Future {
    DB.withSession { implicit session =>
      ApiKeyTable.update(apiKey)
    }
  }

  def delete(key: ApiKeyID): Future[Int] = Future {
    DB.withSession { implicit session =>
      ApiKeyTable.delete(key)
    }
  }
}
