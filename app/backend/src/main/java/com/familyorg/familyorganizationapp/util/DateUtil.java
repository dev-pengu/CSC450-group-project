package com.familyorg.familyorganizationapp.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TimeZone;

public class DateUtil {
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
  private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");


  /**
   * Parses a string in the format yyyy-MM-dd HH:mm to a java.sql.Date object and returns it
   *
   * @param date
   * @return
   * @throws IllegalArgumentException
   */
  public static Date parseDate(String date) throws IllegalArgumentException {
    try {
      return new Date(DATE_FORMAT.parse(date).getTime());
    } catch (ParseException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Parses a string in the format yyyy-MM-dd HH:mm to a java.util.Date object and returns it
   *
   * @param date
   * @return
   * @throws IllegalArgumentException
   */
  public static java.util.Date parseDateTime(String date) throws IllegalArgumentException {
    try {
      return DATE_TIME_FORMAT.parse(date);
    } catch (ParseException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Converts the timestamp from sourceTimezone to targetTimezone and returns a string
   * representation in the format yyyy-MM-dd HH:mm
   *
   * @param timestamp
   * @param sourceTimezone
   * @param targetTimezone
   * @return
   */
  public static String toTimezone(Timestamp timestamp, TimeZone sourceTimezone,
      TimeZone targetTimezone) {
    Calendar calendar = Calendar.getInstance(sourceTimezone);
    calendar.setTime(Date.from(timestamp.toInstant()));

    return calendar.toInstant()
        .atZone(ZoneId.of(targetTimezone.getID()))
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
  }

  public static String toTimezone(java.util.Date timestamp, TimeZone sourceTimezone,
      TimeZone targetTimezone) {
    Calendar calendar = Calendar.getInstance(sourceTimezone);
    calendar.setTime(timestamp);

    return calendar.toInstant()
        .atZone(ZoneId.of(targetTimezone.getID()))
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
  }

  /**
   * Parses a string in the format yyyy-MM-dd HH:mm into a java.sql.Timestamp object
   *
   * @param timestamp
   * @return
   * @throws IllegalArgumentException
   */
  public static Timestamp parseTimestamp(String timestamp) throws IllegalArgumentException {
    try {
      return new Timestamp(DATE_TIME_FORMAT.parse(timestamp).getTime());
    } catch (ParseException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
