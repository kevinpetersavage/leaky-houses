package nest

import akka.actor.{Actor, Props}
import com.firebase.client.Firebase.AuthResultHandler
import com.firebase.client._
import play.api.Logger

import scala.collection.JavaConversions._

object NestActor {
  def props(nestToken: String): Props = Props(new NestActor(nestToken))
}

class NestActor(nestToken: String) extends Actor {

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
        val devices = s.child("devices")
        if (devices != null && devices.getChildren != null) {
          def thermostats = devices.getChildren.filter(_.getKey == "thermostats")
          thermostats.foreach { struct =>
            // update our map of struct ids -> struct names for lookup later
            struct.getChildren.map {
              thermostat =>
              val structName = thermostat.child("name").getValue.toString
              val structState = thermostat.child("target_temperature_c").getValue.toString
              println(structName)
              println(structState)
            }
          }
        }
      }
    }
    case e: FirebaseError => Logger.debug("got firebase error " + e.getMessage)
  }
}
