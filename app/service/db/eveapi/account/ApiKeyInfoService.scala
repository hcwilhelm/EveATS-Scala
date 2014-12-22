package service.db.eveapi.account

import models.eveapi.account.{ApiKeyInfo, ApiKeyInfoTable}
import models.eveats.ApiKeyID
import play.api.Play.current
import play.api.db.slick.DB
import service.db.dbOperationsExecutionContext

import scala.concurrent._

/**
 * ApiKeyInfoService
 */
object ApiKeyInfoService {
  def find(id: ApiKeyID): Future[Option[ApiKeyInfo]] = Future {
    DB.withSession { implicit session =>
      ApiKeyInfoTable.find(id)
    }
  }

  def insert(entity: ApiKeyInfo): Future[ApiKeyID] = Future {
    DB.withSession { implicit session =>
      ApiKeyInfoTable.insert(entity)
    }
  }

  def upadte(entity: ApiKeyInfo) = Future {
    DB.withSession { implicit session =>
      ApiKeyInfoTable.update(entity)
    }
  }

  def insertOrUpdate(entity: ApiKeyInfo) = Future {
    DB.withSession { implicit session =>
      ApiKeyInfoTable.insertOrUpdate(entity)
    }
  }

  def delete(id: ApiKeyID): Future[Int] = Future {
    DB.withSession { implicit session =>
      ApiKeyInfoTable.delete(id)
    }
  }
}
