package service.db.eveapi.account

import models.eveapi.account.{AccountStatus, AccountStatusTable}
import models.eveats.ApiKeyID
import play.api.Play.current
import play.api.db.slick.DB
import service.db.executionContext

import scala.concurrent._

/**
 * AccountStatusService
 *
 */
object AccountStatusService {

  def find(id: ApiKeyID): Future[Option[AccountStatus]] = Future {
    DB.withSession { implicit session =>
      AccountStatusTable.find(id)
    }
  }

  def insert(entity: AccountStatus): Future[ApiKeyID] = Future {
    DB.withSession { implicit session =>
      AccountStatusTable.insert(entity)
    }
  }

  def upadte(entity: AccountStatus): Future[Int] = Future {
    DB.withSession { implicit session =>
      AccountStatusTable.update(entity)
    }
  }
  
  def insertOrUpdate(entity: AccountStatus): Future[Option[ApiKeyID]] = Future {
    DB.withSession { implicit session => 
      AccountStatusTable.insertOrUpdate(entity)
    }
  }

  def delete(id: ApiKeyID): Future[Int] = Future {
    DB.withSession { implicit session =>
      AccountStatusTable.delete(id)
    }
  }
}
