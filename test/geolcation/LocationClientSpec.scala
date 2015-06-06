package geolcation

import com.xebialabs.restito.builder.stub.StubHttp._
import com.xebialabs.restito.semantics.Action._
import com.xebialabs.restito.semantics.Condition._
import com.xebialabs.restito.server.StubServer
import geolocation.LocationClient
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}
import play.api.test.FakeApplication
import play.api.test.Helpers._

import scala.concurrent.Await
import scala.concurrent.duration._


class LocationClientSpec extends FlatSpec with Matchers with BeforeAndAfterEach {
  val exampleResponse = """{"results":[{"formatted_address" : "London SE15 6NP, UK", "geometry" : { "location" : { "lat" : 51.4857881, "lng" : -0.073819}}}], "status" : "OK"}"""
  var server: StubServer = _

  override def beforeEach {
    server = new StubServer().run()
  }

  override def afterEach {
    server.stop()
  }

  "the client" should "get location" in {
    running(FakeApplication()) {
      val appId = "1a"

      whenHttp(server)
        .`match` (get("/maps/api/geocode/json"), parameter("address","se156np,uk"), parameter("key",appId))
        .`then`(ok(), stringContent(exampleResponse))

      val locationFuture = new LocationClient("http", s"localhost:${server.getPort}", appId).getLocation("se156np","uk")
      val location = Await.result(locationFuture, 1 second).get

      location should be((51.4857881, -0.073819))
    }

  }
}
