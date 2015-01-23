import actors.emdr.EMDRService
import play.api._

object Global extends GlobalSettings {

  override def onStart(app: Application) {

    // Start EMDR Service
    //EMDRService.start()

    Logger.info("Application has started")
  }

  override def onStop(app: Application) {
    //Stop EMDR Service
    //EMDRService.stop()

    Logger.info("Application shutdown...")
  }
}