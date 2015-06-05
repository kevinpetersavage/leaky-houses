package nest

import com.firebase.client.DataSnapshot


import scala.collection.JavaConversions._

class NestParser {
  def parse(dataSnapshot: DataSnapshot): NestReading = {
    val thermostats = readThermostats(dataSnapshot)
    val addresses = readAddresses(dataSnapshot)

    val thermostat = thermostats.head
    val address = addresses.head

    NestReading(thermostat._1, thermostat._2, address._1, address._2)
  }

  def readAddresses(dataSnapshot: DataSnapshot): Iterable[(String, String)] = {
    val structures: Option[DataSnapshot] = Option(dataSnapshot.child("structures"))
    val structureList = getAllChildren(structures)
    val addresses = structureList.map {
      structure =>
        val postcode = structure.child("postal_code").getValue.toString
        val country = structure.child("country_code").getValue.toString
        (postcode, country)
    }
    addresses
  }

  def readThermostats(dataSnapshot: DataSnapshot) = {
    val devices = Option(dataSnapshot.child("devices"))
    val deviceTypes = getAllChildren(devices)
    def thermostats = deviceTypes.filter(_.getKey == "thermostats")
    thermostats.flatMap { struct =>
      struct.getChildren.map {
        thermostat =>
          val name = thermostat.child("name").getValue.toString
          val temperature = thermostat.child("target_temperature_c").getValue.toString.toDouble
          (name, temperature)
      }
    }
  }

  def getAllChildren(structures: Option[DataSnapshot]): Iterable[DataSnapshot] = {
    structures.map(_.getChildren).map(_.toList).getOrElse(List().toIterable)
  }
}

case class NestReading(name:String,targetTempC:Double,postcode:String,country:String)