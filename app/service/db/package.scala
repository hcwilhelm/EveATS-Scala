package service

import play.api.libs.concurrent.Akka
import play.api.Play.current

import scala.concurrent.ExecutionContext

/**
 * Created by hcwilhelm on 11.12.14.
 */
package object db {
  implicit val dbOperationsExecutionContext: ExecutionContext = Akka.system.dispatchers.lookup("contexts.db-operations")

}
