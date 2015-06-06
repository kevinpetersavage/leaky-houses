import akka.actor.Props
import database.ReadingLogger
import geolocation.LocationClient
import play.api._
import play.libs.Akka
import nest.{NestParser, NestActor}
import weather.OpenWeatherMapClient

object Global extends GlobalSettings {
  override def onStart(app: Application) {


    Akka.system.actorOf(Props(
      new NestActor(
        System.getProperty("nest.access.token"),
        new NestParser(),
        new OpenWeatherMapClient("http","api.openweathermap.org", System.getProperty("weather.appid")),
        new LocationClient("https", "maps.googleapis.com", System.getProperty("location.appid")),
        new ReadingLogger()
      )))
  }
}
