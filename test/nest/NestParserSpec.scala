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


    val result = new NestParser().parse(snapshot)

    result should contain (("Living Room (901E)","25"))
  }
}
