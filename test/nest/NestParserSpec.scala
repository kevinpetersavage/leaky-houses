package nest

import com.firebase.client.DataSnapshot
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import scala.collection.JavaConverters._

class NestParserSpec extends FlatSpec with Matchers with MockitoSugar{
  "the parser" should "parse example data snapshot" in {
    val snapshot = mock[DataSnapshot]
    val devices = mock[DataSnapshot]
    when(snapshot.child("devices")).thenReturn(devices)

    val thermostats = mock[DataSnapshot]
    when(thermostats.getKey).thenReturn("thermostats")
    val thermostat = mock[DataSnapshot]

    val name = mock[DataSnapshot]
    val temperatureReading = mock[DataSnapshot]
    when(thermostat.child("name")).thenReturn(name)
    when(thermostat.child("target_temperature_c")).thenReturn(temperatureReading)
    when(name.getValue).thenReturn("Living Room (901E)","Living Room (901E)")
    when(temperatureReading.getValue).thenReturn("25", "25")
    when(thermostats.getChildren).thenReturn(List(thermostat).toIterable.asJava)

    val smokeDetectors = mock[DataSnapshot]
    when(smokeDetectors.getKey).thenReturn("smoke_co2_alarms")
    val typesOfDevice = List(thermostats, smokeDetectors).toIterable.asJava
    when(devices.getChildren).thenReturn(typesOfDevice)

    val structures = mock[DataSnapshot]
    when(snapshot.child("structures")).thenReturn(structures)
    val myHouse = mock[DataSnapshot]
    when(structures.getChildren).thenReturn(List(myHouse).toIterable.asJava)

    val country = mock[DataSnapshot]
    val postCode = mock[DataSnapshot]
    when(myHouse.child("country_code")).thenReturn(country)
    when(myHouse.child("postal_code")).thenReturn(postCode)
    when(country.getValue).thenReturn("US","US")
    when(postCode.getValue).thenReturn("12345","12345")

    val result = new NestParser().parse(snapshot)

    result should be (NestReading("Living Room (901E)",25.0,"12345","US"))
  }
}
