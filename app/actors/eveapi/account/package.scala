package actors.eveapi

/**
 * Created by hcwilhelm on 07.01.15.
 */
package object account {

  /**
   * Service Exceptions
   *
   * @param msg
   */
  sealed class ServiceException(msg: String) extends RuntimeException(msg)
  case class ApiKeyNotFound(msg: String) extends ServiceException(msg)
}
