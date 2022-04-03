package com.familyorg.familyorganizationapp.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
public class CalendarTest {
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
    System.out.println("Running [CalendarTest] testCreate...");
    /* Given */
    session.beginTransaction();
    Family family = new Family("Test Name", "000000", "America/Chicago", null, null);
    Long familId = (Long) session.save(family);
    assertTrue(familId > 0);
    Calendar calendar = new Calendar(family, "Test Description");

    /* When */
    Long id = (Long) session.save(calendar);
    session.getTransaction().commit();

    /* Then */
    assertTrue(id > 0);
  }

  @Test
  @Order(2)
  public void testGet() {
    System.out.println("Running [CalendarTest] testGet...");
    /* Given */
    Long id = 1l;

    /* When */
    Calendar calendar = session.find(Calendar.class, id);

    /* Then */
    assertEquals("Test Description", calendar.getDescription());
  }

  @Test
  @Order(3)
  public void testUpdate() {
    System.out.println("Running [CalendarTest] testUpdate...");
    /* Given */
    Long id = 1l;
    Calendar calendar = new Calendar(session.find(Family.class, 1l), "Updated Description");
    calendar.setId(id);

    /* When */
    session.beginTransaction();
    session.update(calendar);
    session.getTransaction().commit();

    Calendar updatedCalendar = session.find(Calendar.class, 1l);

    /* Then */
    assertEquals("Updated Description", updatedCalendar.getDescription());
  }

  @Test
  @Order(4)
  public void testList() {
    System.out.println("Running [CalendarTest] testList...");
    /* Given */
    String queryString = "from Calendar";

    /* When */
    Query<Calendar> query = session.createQuery(queryString, Calendar.class);
    List<Calendar> resultList = query.getResultList();

    /* Then */
    assertFalse(resultList.isEmpty());
  }

  @Test
  @Order(5)
  public void testDelete() {
    System.out.println("Running [CalendarTest] testDelete...");
    /* Given */
    Long id = 1l;
    Calendar calendar = session.find(Calendar.class, id);

    /* When */
    session.beginTransaction();
    session.delete(calendar);
    session.getTransaction().commit();
    Calendar deletedCalendar = session.find(Calendar.class, id);

    /* Then */
    assertNull(deletedCalendar);
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
