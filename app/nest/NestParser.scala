package nest

import com.firebase.client.DataSnapshot


import scala.collection.JavaConversions._

class NestParser {
  def parse(dataSnapshot: DataSnapshot): List[(String, String)] = {
    val devices = Option(dataSnapshot.child("devices"))
    val deviceTypes = devices.map(_.getChildren).map(_.toList).getOrElse(List().toIterable)
    def thermostats = deviceTypes.filter(_.getKey == "thermostats")
    thermostats.flatMap { struct =>
      struct.getChildren.map {
        thermostat =>
          val structName = thermostat.child("name").getValue.toString
          val structState = thermostat.child("target_temperature_c").getValue.toString
          (structName, structState)
      }
    }.toList
  }
}
