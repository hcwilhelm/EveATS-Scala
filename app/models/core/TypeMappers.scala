package models.core

import java.sql.{Date, Timestamp}
import org.joda.time.{Duration, LocalDate, DateTime}

trait TypeMappers { self: HasJdbcDriver =>
  import driver.simple._

  trait CustomTypeMappers {
    implicit val dateTimeMapper: BaseColumnType[DateTime] = MappedColumnType.base[DateTime, Timestamp](
      dateTime => new Timestamp(dateTime.getMillis),
      timestamp => new DateTime(timestamp.getTime)
    )

    implicit val localDateMapper: BaseColumnType[LocalDate] = MappedColumnType.base[LocalDate, Date](
      localDate => new Date(localDate.toDate.getTime),
      date => new LocalDate(date.getTime)
    )

    implicit val durationTypeMapper: BaseColumnType[Duration] = MappedColumnType.base[Duration, Long](
      duration => duration.getMillis,
      millis => new Duration(millis)
    )
  }

  object CustomTypeMappers extends CustomTypeMappers
}
