package weather

import com.xebialabs.restito.builder.stub.StubHttp.whenHttp
import com.xebialabs.restito.semantics.Action._
import com.xebialabs.restito.semantics.Condition._
import com.xebialabs.restito.server.StubServer
;
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration._


import play.api.test._
import play.api.test.Helpers._

class OpenWeatherMapClientSpec extends FlatSpec with Matchers with BeforeAndAfterEach {
  val exampleResponse = """{"coord":{"lon":0.1,"lat":51.5},"sys":{"message":0.0219,"country":"GB","sunrise":1433389535,"sunset":1433448620},"weather":[{"id":803,"main":"Clouds","description":"broken clouds","icon":"04d"}],"base":"stations","main":{"temp":10.807,"temp_min":10.807,"temp_max":10.807,"pressure":1032.25,"sea_level":1040.15,"grnd_level":1032.25,"humidity":70},"wind":{"speed":0.77,"deg":8.50027},"clouds":{"all":56},"dt":1433402853,"id":7302135,"name":"Abbey Wood","cod":200}"""

  var server: StubServer = _


  override def beforeEach {
    server = new StubServer().run()
  }

  override def afterEach {
    server.stop()
  }

  "the client" should "get temperature" in {
    running(FakeApplication()) {
      val appId = "1a"

      whenHttp(server)
        .`match` (get("/data/2.5/weather"), parameter("lat","51.5"), parameter("lon","0.1"), parameter("units","metric"), parameter("APPID",appId))
        .`then`(ok(), stringContent(exampleResponse))

      val temperatureFuture = new OpenWeatherMapClient("http", s"localhost:${server.getPort}", appId).getTemperature(51.5, 0.1)
      val temperature = Await.result(temperatureFuture, 1 second).get

      temperature should be(10.807)
    }
  }
}
