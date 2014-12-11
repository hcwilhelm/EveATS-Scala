package actors

import play.api.libs.concurrent.Akka
import play.api.Play.current

import scala.concurrent.ExecutionContext

/**
 * Created by hcwilhelm on 09.12.14.
 */
object Contexts {
  implicit val dbOperationsExecutionContext: ExecutionContext = Akka.system.dispatchers.lookup("db-operations")
}
