package service.db.eveapi.account

import models.eveapi.account.{CharactersToApiKeysTable, ApiKeyInfo, ApiKeyInfoTable}
import models.eveapi.eve.Character
import models.eveats.ApiKeyID
import play.api.Play.current
import play.api.db.slick.DB
import service.db.executionContext

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

  def update(entity: ApiKeyInfo) = Future {
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

  def updateCharacters(id: ApiKeyID, chars: List[Character]): Future[Option[Int]] = Future {
    DB.withSession { implicit session =>
      CharactersToApiKeysTable.deleteByApiKey(id)
      CharactersToApiKeysTable.insert(chars.map(_.id -> id))
    }
  }

  def findCharacters(id: ApiKeyID): Future[Seq[Character]] = Future {
    DB.withSession { implicit session =>
      CharactersToApiKeysTable.findCharacters(id)
    }
  }
}
