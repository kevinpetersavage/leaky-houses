package database

import nest.NestReading
import play.api.db._
import play.api.Play.current

class ReadingLogger{
  val notUsed = -1

  def log(reading: NestReading, externalTempC: Double) = {
    DB.withConnection { conn =>
      conn.createStatement.executeUpdate(
        s"INSERT INTO Reading (name,targetTempC,postcode,country,externalTempC) VALUES" +
          s"('${reading.name}',${reading.targetTempC},'${reading.postcode}','${reading.country}',$externalTempC)"
      )
    }
  }
}
