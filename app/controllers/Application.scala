package controllers

import actors.eveapi.account.AccountStatusService
import models.eveats.{ApiKeyID, ApiKey, UserID, User}
import play.api._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc._
import service.db.eveats.{UsersToApiKeysService, ApiKeyService, UserService}
import akka.actor._
import play.api.libs.concurrent.Akka
import play.api.Play.current
import xmlparser.EveApiError

import scala.util.{Failure, Success}

object Application extends Controller {

//  sealed trait State
//  case object Start extends State
//  case object End extends State
//
//  final class Data
//
//  class TestActor extends FSM[State, Data] {
//
//
//
//    startWith(Start, new Data())
//
//    when(Start) {
//      case Event(ApiKeyID(id), Uninitialized) =>
//        println("Start State reached")
//        goto(End)
//    }
//
//    when(End) {
//      case Event(_, _) =>
//        println("End state reached")
//        stay()
//    }
//
//    onTransition {
//      case Start -> End => println("Transitioning to End state")
//    }
//
//    initialize()
//
//    override def preStart = {
//      context.self ! ApiKeyID(2)
//    }
//  }



  def index = Action {

    AccountStatusService().get(ApiKeyID(1963281)).onComplete {
      case Success(s) => println(s)
      case Failure(EveApiError(code, msg)) => println("ErrorCode : " + code + " " + msg)
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