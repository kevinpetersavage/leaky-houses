package database

import nest.NestReading
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}
import play.api.Application
import play.api.Play.current
import play.api.db.{DB, _}
import play.api.db.evolutions._
import play.api.test.FakeApplication
import play.api.test.Helpers._


class ReadingLoggerSpec extends FlatSpec with Matchers with BeforeAndAfterEach {
  "the reading log actor" should "log things to the db" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      val dbCache = Application.instanceCache[DBApi]
      val db = dbCache(current).database("default")
      Evolutions.applyEvolutions(db)

      new ReadingLogger().log(NestReading("living room", 20.0, "se15", "uk"), 11.3)

      DB.withConnection { conn =>
        val rs = conn.createStatement.executeQuery("SELECT * FROM Reading")

        rs.next() should be (true)
        rs.getString("name") should be("living room")
        rs.getDouble("targetTempC") should be(20.0)
        rs.getString("postcode") should be("se15")
        rs.getString("country") should be("uk")
        rs.getDouble("externalTempC") should be(11.3)
      }
    }
  }
}