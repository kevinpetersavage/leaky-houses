package nest

import akka.actor.Actor
import com.firebase.client.Firebase.AuthResultHandler
import com.firebase.client._
import geolocation.LocationClient
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits._
import weather.OpenWeatherMapClient

class NestActor(nestToken: String, nestParser: NestParser, weather: OpenWeatherMapClient, locator: LocationClient) extends Actor {

  val fb = new Firebase("https://developer-api.nest.com")

  fb.authWithCustomToken(nestToken, new AuthResultHandler {
    override def onAuthenticated(authData: AuthData): Unit = {
      println("fb auth success: " + authData)

      // when we've successfully authed, add a change listener to the whole tree
      fb.addValueEventListener(new ValueEventListener {
        def onDataChange(snapshot: DataSnapshot) {
          // when data changes we send our receive block an update
          self ! snapshot
        }

        def onCancelled(err: FirebaseError) {
          // on an err we should just bail out
          self ! err
        }
      })
    }

    override def onAuthenticationError(e: FirebaseError): Unit =  Logger.debug("fb auth error: " + e.getMessage)
  })

  override def receive: Receive = {
    case s: DataSnapshot => {
      try {
        val nestReading = nestParser.parse(s)
        println(nestReading)

        for (
          location <- locator.getLocation(nestReading.postcode, nestReading.country);
          temperature <- location.map((weather.getTemperature _).tupled)
        ) {
          temperature.onComplete(_.get.foreach(println))
        }
      } catch {
        case e : Exception => Logger.error("exception parsing nest data", e);
      }
    }
    case e: FirebaseError => Logger.debug(s"got firebase error: ${e.getMessage}")
  }
}
