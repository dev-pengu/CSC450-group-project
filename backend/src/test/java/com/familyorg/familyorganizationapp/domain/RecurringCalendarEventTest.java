package com.familyorg.familyorganizationapp.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import com.familyorg.familyorganizationapp.utility.HibernateUtil;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RecurringCalendarEventTest {
  private static SessionFactory sessionFactory;
  private Session session;

  Timestamp originalStartDatetime;
  Timestamp originalEndDatetime;

  @BeforeAll
  public static void setup() {
    sessionFactory = HibernateUtil.getSessionFactory();
    System.out.println("SessionFactory created");
  }

  @AfterAll
  public static void tearDown() {
    if (sessionFactory != null) {
      sessionFactory.close();
    }
    System.out.println("SessionFactory destroyed");
  }

  @Test
  @Order(1)
  public void testCreate() {
    System.out.println("Running [RecurringCalendarEventTest] testCreate...");
    /* Given */
    session.beginTransaction();
    User user = new User("Test", "User", "testuser", "password", "testuser@test.com", null);
    Family family = new Family("Test Name", "000000", "America/Chicago", null, null);
    Calendar calendar = new Calendar(family, "Test Description");
    Long userId = (Long) session.save(user);
    Long familId = (Long) session.save(family);
    assertTrue(userId > 0);
    assertTrue(familId > 0);
    Long calendarId = (Long) session.save(calendar);
    assertTrue(calendarId > 0);

    CalendarEvent calendarEvent = new CalendarEvent();
    calendarEvent.setAllDay(false);
    calendarEvent.setFamilyEvent(true);
    calendarEvent.setStartDatetime(Timestamp.from(Instant.now()));
    calendarEvent.setEndDatetime(Timestamp.from(Instant.now()));
    calendarEvent.setDescription("Test description");
    calendarEvent.setNotes("Test notes");
    calendarEvent.setCreatedBy(user);
    calendarEvent.setCalendar(calendar);
    calendarEvent.setCreatedDatetime(Timestamp.from(Instant.now()));
    calendarEvent.setTimezone("America/Chicago");
    Long eventId = (Long) session.save(calendarEvent);
    assertTrue(eventId > 0);

    RecurringCalendarEvent recurringEvent = new RecurringCalendarEvent();
    originalStartDatetime = Timestamp.from(Instant.now());
    originalEndDatetime = Timestamp.from(Instant.now());
    recurringEvent.setStartDatetime(originalStartDatetime);
    recurringEvent.setEndDatetime(originalEndDatetime);
    recurringEvent.setOriginatingEvent(calendarEvent);


    /* When */
    Long id = (Long) session.save(recurringEvent);
    session.getTransaction().commit();

    /* Then */
    assertTrue(id > 0);
  }

  @Test
  @Order(2)
  public void testGet() {
    System.out.println("Running [RecurringCalendarEventTest] testGet...");
    /* Given */
    Long id = 1l;

    /* When */
    RecurringCalendarEvent event = session.find(RecurringCalendarEvent.class, id);

    /* Then */
    assertNotNull(event);
  }

  @Test
  @Order(3)
  public void testUpdate() {
    System.out.println("Running [RecurringCalendarEventTest] testUpdate...");
    /* Given */
    Long id = 1l;
    RecurringCalendarEvent recurringEvent = session.find(RecurringCalendarEvent.class, id);
    recurringEvent.setStartDatetime(Timestamp.from(Instant.now()));
    recurringEvent.setEndDatetime(Timestamp.from(Instant.now()));

    /* When */
    session.beginTransaction();
    session.update(recurringEvent);
    session.getTransaction().commit();

    RecurringCalendarEvent updatedRecurringEvent = session.find(RecurringCalendarEvent.class, id);

    /* Then */
    assertNotEquals(originalStartDatetime, updatedRecurringEvent.getStartDatetime());
    assertNotEquals(originalEndDatetime, updatedRecurringEvent.getEndDatetime());
  }

  @Test
  @Order(4)
  public void testList() {
    System.out.println("Running [RecurringCalendarEventTest] testList...");
    /* Given */
    String queryString = "from RecurringCalendarEvent";

    /* When */
    Query<RecurringCalendarEvent> query =
        session.createQuery(queryString, RecurringCalendarEvent.class);
    List<RecurringCalendarEvent> resultList = query.getResultList();

    /* Then */
    assertFalse(resultList.isEmpty());
  }

  @Test
  @Order(5)
  public void testDelete() {
    System.out.println("Running [RecurringCalendarEventTest] testDelete...");
    /* Given */
    Long id = 1l;
    RecurringCalendarEvent recurringEvent = session.find(RecurringCalendarEvent.class, id);

    /* When */
    session.beginTransaction();
    session.delete(recurringEvent);
    session.getTransaction().commit();
    RecurringCalendarEvent deletedRecurringEvent = session.find(RecurringCalendarEvent.class, id);

    /* Then */
    assertNull(deletedRecurringEvent);
  }

  @BeforeEach
  public void openSession() {
    session = sessionFactory.openSession();
    System.out.println("Session created");
  }

  @AfterEach
  public void closeSession() {
    if (session != null) {
      session.close();
    }
    System.out.println("Session destroyed");
  }
}
