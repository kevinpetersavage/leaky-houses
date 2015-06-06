package geolocation

import play.api.libs.json.Json
import play.api.libs.ws.WS

import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._

class LocationClient(scheme: String, host: String, appId: String) {
  def getLocation(postCode: String, country: String) = {
    val url = s"$scheme://$host/maps/api/geocode/json?address=$postCode,$country&key=$appId"
    val eventualResponse = WS.url(url).get()
    eventualResponse
      .map(response => (Json.parse(response.body) \ "results")(0))
      .map(place => place \ "geometry" \ "location")
      .map(location => (location \ "lat", location \ "lng"))
      .map(latLong => map(latLong)(_.toOption))
      .map(latLong => map(latLong)(_.map(_.toString().toDouble)))
      .map{
        case (Some(lat), Some(long)) => Some((lat, long))
        case _ => None
      }
  }

  def map[A, B](as: (A, A))(f: A => B) = as match { case (a1, a2) => (f(a1), f(a2)) }
}
