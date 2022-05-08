package com.familyorg.familyorganizationapp.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import com.familyorg.familyorganizationapp.util.HibernateUtil;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CalendarEventTest {
  private static SessionFactory sessionFactory;
  private Session session;

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
    System.out.println("Running [CalendarEventTest] testCreate...");
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
    calendarEvent.setCreatedDatetime(Timestamp.from(Instant.now()));
    calendarEvent.setCalendar(calendar);
    calendarEvent.setTimezone("America/Chicago");

    /* When */
    Long id = (Long) session.save(calendarEvent);
    session.getTransaction().commit();

    /* Then */
    assertTrue(id > 0);
  }

  @Test
  @Order(2)
  public void testGet() {
    System.out.println("Running [CalendarEventTest] testGet...");
    /* Given */
    Long id = 1l;

    /* When */
    CalendarEvent event = session.find(CalendarEvent.class, id);

    /* Then */
    assertEquals("Test description", event.getDescription());
  }

  @Test
  @Order(3)
  public void testUpdate() {
    System.out.println("Running [CalendarEventTest] testUpdate...");
    /* Given */
    Long id = 1L;
    CalendarEvent calendarEvent = session.find(CalendarEvent.class, id);
    calendarEvent.setAllDay(true);


    /* When */
    session.beginTransaction();
    session.update(calendarEvent);
    session.getTransaction().commit();

    CalendarEvent updatedEvent = session.find(CalendarEvent.class, id);

    /* Then */
    assertTrue(updatedEvent.isAllDay());
  }

  @Test
  @Order(4)
  public void testList() {
    System.out.println("Running [CalendarEventTest] testList...");
    /* Given */
    String queryString = "from CalendarEvent";

    /* When */
    Query<CalendarEvent> query = session.createQuery(queryString, CalendarEvent.class);
    List<CalendarEvent> resultList = query.getResultList();

    /* Then */
    assertFalse(resultList.isEmpty());
  }

  @Test
  @Order(5)
  public void testDelete() {
    System.out.println("Running [CalendarEventTest] testDelete...");
    /* Given */
    Long id = 1l;
    CalendarEvent event = session.find(CalendarEvent.class, id);

    /* When */
    session.beginTransaction();
    session.delete(event);
    session.getTransaction().commit();
    CalendarEvent deletedEvent = session.find(CalendarEvent.class, id);

    /* Then */
    assertNull(deletedEvent);
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
