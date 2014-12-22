package service.db.eveapi.eve

import models.eveapi.eve._
import models.eveats.ApiKeyID
import play.api.Play.current
import play.api.db.slick.DB
import service.db.dbOperationsExecutionContext

import scala.concurrent._


/**
 * CharacterAffiliationService
 */
object CharacterAffiliationService {

  def findCharacter(id: CharacterID): Future[Option[Character]] = Future {
    DB.withSession { implicit session =>
      CharacterTable.find(id)
    }
  }

  def insertCharacter(entity: Character): Future[CharacterID] = Future {
    DB.withSession { implicit session =>
      CharacterTable.insert(entity)
    }
  }

  def updateCharacter(entity: Character): Future[Int] = Future {
    DB.withSession { implicit session =>
      CharacterTable.update(entity)
    }
  }

  def insertOrUpdateCharacter(entity: Character): Future[CharacterID] = Future {
    DB.withSession { implicit session =>
      CharacterTable.insertOrUpdate(entity)
    }
  }

  def findCorporation(id: CorporationID): Future[Option[Corporation]] = Future {
    DB.withSession { implicit session =>
      CorporationTable.find(id)
    }
  }

  def insertCorporation(entity: Corporation): Future[CorporationID] = Future {
    DB.withSession { implicit session =>
      CorporationTable.insert(entity)
    }
  }

  def updateCorporation(entity: Corporation): Future[Int] = Future {
    DB.withSession { implicit session =>
      CorporationTable.update(entity)
    }
  }

  def insertOrUpdateCorporation(entity: Corporation): Future[CorporationID] = Future {
    DB.withSession { implicit session =>
      CorporationTable.insertOrUpdate(entity)
    }
  }

  def insertAffiliation(apiKeyID: ApiKeyID, chars: Set[Character], corps: Set[Corporation]) = ???
}
