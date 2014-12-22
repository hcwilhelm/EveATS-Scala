package service.db.eveats

import models.eveats._
import play.api.Play.current
import play.api.db.slick.DB
import service.db.dbOperationsExecutionContext

import scala.concurrent._


object UsersToApiKeysService {

  def findUsers(apiKeyID: ApiKeyID): Future[Seq[User]] = Future {
    DB.withSession { implicit session =>
      UsersToApiKeysTable.findUsers(apiKeyID)
    }
  }

  def findApiKeys(userID: UserID): Future[Seq[ApiKey]] = Future {
    DB.withSession { implicit session =>
      UsersToApiKeysTable.findApiKeys(userID)
    }
  }

  def insert(rel: (UserID, ApiKeyID)): Future[Int] = Future {
    DB.withSession { implicit session =>
      UsersToApiKeysTable.insert(rel)
    }
  }

  def delete(rel: (UserID, ApiKeyID)): Future[Int] = Future {
    DB.withSession { implicit session =>
      UsersToApiKeysTable.delete(rel)
    }
  }
}
