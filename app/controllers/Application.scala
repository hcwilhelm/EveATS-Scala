package controllers

import actors.eveapi.account.{ApiKeyInfoService, AccountStatusService}
import models.eveats.{ApiKey, ApiKeyID}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc._
import service.db.eveats.ApiKeyService
import xmlparser.{EveApiError, InvalidXML}

import scala.util.{Failure, Success}

object Application extends Controller {

  def index = Action {

    AccountStatusService().get(ApiKeyID(1963281)).onComplete {
      case Success(s) => println(s)
      case Failure(ex: EveApiError) => println(ex)
      case Failure(ex: InvalidXML) => println(ex)
      case Failure(ex: actors.eveapi.account.ApiKeyNotFound) =>
        ApiKeyService.insert(ApiKey(ApiKeyID(1963281), "oMJ92FB2hFQgFaMG8wprX9B18lpdMvAazo7S7dX3fsc29zPBJn2PUTKmKT3052Gf"))
        .map(_ => println(ex))
      case Failure(ex) => println(ex)
    }

    ApiKeyInfoService().getInfo(ApiKeyID(1963281)).onComplete {
      case Success(s) => println(s)
      case Failure(ex) => println(ex)
    }

    ApiKeyInfoService().getChars(ApiKeyID(1963281)).onComplete {
      case Success(s) => println(s)
      case Failure(ex) => println(ex)
    }

//    val userID = UserService.insert(User(None, "hcwilhelm", "foo"))
//    val keyID_1 = ApiKeyService.insert(ApiKey(ApiKeyID(1), "#1"))
//    val keyID_2 = ApiKeyService.insert(ApiKey(ApiKeyID(2), "#2"))
//
//    for {
//      userID <- userID
//      keyID_1 <- keyID_1
//      keyID_2 <- keyID_2
//
//      _ <- UsersToApiKeysService.insert(userID -> keyID_1)
//      _ <- UsersToApiKeysService.insert(userID -> keyID_2)
//    }(println("Insert Done"))


//    val testActor = Akka.system.actorOf(Props[TestActor], name = "TestActor")


    Ok(views.html.index("Your new application is ready."))
  }


}