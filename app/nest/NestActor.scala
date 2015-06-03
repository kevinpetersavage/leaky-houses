package nest

import akka.actor.{Actor, Props}
import com.firebase.client.Firebase.AuthResultHandler
import com.firebase.client._
import play.api.Logger


class NestActor(nestToken: String, nestParser: NestParser) extends Actor {

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
        nestParser.parse(s).foreach(println)
      } catch {
        case e : Exception => Logger.error("exception parsing nest data", e);
      }
    }
    case e: FirebaseError => Logger.debug(s"got firebase error: ${e.getMessage}")
  }
}
