package com.familyorg.familyorganizationapp.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.sql.Date;
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
public class EventRepetitionScheduleTest {
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
    System.out.println("Running [EventRepetitionScheduleTest] testCreate...");
    /* Given */
    session.beginTransaction();

    EventRepetitionSchedule schedule = new EventRepetitionSchedule();
    schedule.setFrequency(CalendarRepetitionFrequency.DAILY);
    schedule.setInterval(7);
    schedule.setCount(10);
    schedule.setStartDate(Date.valueOf("2022-04-01"));

    /* When */
    Long id = (Long) session.save(schedule);
    session.getTransaction().commit();

    /* Then */
    assertTrue(id > 0);
  }

  @Test
  @Order(2)
  public void testGet() {
    System.out.println("Running [EventRepetitionScheduleTest] testGet...");
    /* Given */
    Long id = 1l;

    /* When */
    EventRepetitionSchedule schedule = session.find(EventRepetitionSchedule.class, id);

    /* Then */
    assertEquals(CalendarRepetitionFrequency.DAILY, schedule.getFrequency());
  }

  @Test
  @Order(3)
  public void testUpdate() {
    System.out.println("Running [EventRepetitionScheduleTest] testUpdate...");
    /* Given */
    Long id = 1l;
    EventRepetitionSchedule schedule = new EventRepetitionSchedule();
    schedule.setFrequency(CalendarRepetitionFrequency.WEEKLY);
    schedule.setInterval(7);
    schedule.setCount(10);
    schedule.setStartDate(Date.valueOf("2022-04-01"));
    schedule.setId(id);

    /* When */
    session.beginTransaction();
    session.update(schedule);
    session.getTransaction().commit();

    EventRepetitionSchedule updatedSchedule = session.find(EventRepetitionSchedule.class, id);

    /* Then */
    assertEquals(CalendarRepetitionFrequency.WEEKLY, updatedSchedule.getFrequency());
  }

  @Test
  @Order(4)
  public void testList() {
    System.out.println("Running [EventRepetitionScheduleTest] testList...");
    /* Given */
    String queryString = "from EventRepetitionSchedule";

    /* When */
    Query<EventRepetitionSchedule> query =
        session.createQuery(queryString, EventRepetitionSchedule.class);
    List<EventRepetitionSchedule> resultList = query.getResultList();

    /* Then */
    assertFalse(resultList.isEmpty());
  }

  @Test
  @Order(5)
  public void testDelete() {
    System.out.println("Running [EventRepetitionScheduleTest] testDelete...");
    /* Given */
    Long id = 1l;
    EventRepetitionSchedule schedule = session.find(EventRepetitionSchedule.class, id);

    /* When */
    session.beginTransaction();
    session.delete(schedule);
    session.getTransaction().commit();
    EventRepetitionSchedule deletedSchedule = session.find(EventRepetitionSchedule.class, id);

    /* Then */
    assertNull(deletedSchedule);
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
