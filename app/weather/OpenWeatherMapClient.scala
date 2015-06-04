package weather

import play.api.libs.json.Json
import play.api.libs.ws.WS

import scala.concurrent.Future
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._

class OpenWeatherMapClient(host: String, appId: String) {
  def getTemperature(lat: Double, lon: Double): Future[Option[Double]] = {
    val url = s"http://$host/data/2.5/weather?lat=$lat&lon=$lon&units=metric&APPID=423a28a1903f2d857513379bb5939d77"
    val eventualResponse = WS.url(url).get()
    eventualResponse
      .map(response => Json.parse(response.body) \ "main" \ "temp")
      .map(temperatureNode => temperatureNode.toOption.map(_.toString().toDouble))
  }
}
