package se.citerus.dddsample.application.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * A few utils for working with LocalDateTime in tests.
 *
 */
public final class DateTestUtil {

  /**
   * @param date date string as yyyy-MM-dd
   * @return LocalDateTime representation
   */
  public static LocalDateTime toDate(final String date) {
    return toDate(date, "00:00");
  }

  /**
   * @param date date string as yyyy-MM-dd
   * @param time time string as HH:mm
   * @return LocalDateTime representation
   */
  public static LocalDateTime toDate(final String date, final String time) {
    return LocalDateTime.parse(date + " " + time, DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm"));
  }

  /**
   * @param ms the number of milliseconds from 1970-01-01T00:00:00Z
   * @return LocalDateTime representation
   */
  public static LocalDateTime ts(final long ms) {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(ms), ZoneId.systemDefault());
  }

  /**
   * Prevent instantiation.
   */
  private DateTestUtil() {
  }
}
