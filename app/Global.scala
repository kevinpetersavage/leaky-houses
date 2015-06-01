import akka.actor.Props
import play.api._
import play.libs.Akka
import nest.NestActor

object Global extends GlobalSettings {
  override def onStart(app: Application) {
    Akka.system.actorOf(Props(new NestActor(System.getProperty("access.token"))))
  }
}
