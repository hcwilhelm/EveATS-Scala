package service.db.eveats

import models.eveats._
import play.api.db.slick.DB
import play.api.Play.current
import service.db.dbOperationsExecutionContext

import scala.concurrent._

/**
 * UserService
 *
 */
object UserService {

  def findByID(userID: UserID): Future[Option[User]] = Future {
    DB.withSession { implicit session =>
      UserTable.find(userID)
    }
  }

  def findByEmail(email: String): Future[Option[User]] = Future {
    DB.withSession { implicit session =>
      UserTable.findByEmail(email)
    }
  }

  def insert(user: User): Future[UserID] = Future {
    DB.withSession { implicit session =>
      UserTable.insert(user)
    }
  }

  def update(user: User): Future[Option[Int]] = Future {
    DB.withSession { implicit session =>
      UserTable.update(user)
    }
  }

  def delete(userID: UserID): Future[Int] = Future {
    DB.withSession { implicit session =>
      UserTable.delete(userID)
    }
  }
}
