package com.familyorg.familyorganizationapp.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.familyorg.familyorganizationapp.utility.HibernateUtil;
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

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PollTest {
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
    Poll poll = new Poll();
    poll.setDescription("Test description");
    poll.setCloseDateTime(Timestamp.from(Instant.now()));
    poll.setCreatedDatetime(Timestamp.from(Instant.now()));
    poll.setTimezone("America/Chicago");

    /* When */
    Long id = (Long) session.save(poll);
    session.getTransaction().commit();

    /* Then */
    assertTrue(id > 0);
  }

  @Test
  @Order(2)
  public void testGet() {
    System.out.println("Running [CalendarTest] testGet...");
    /* Given */
    Long id = 1L;

    /* When */
    Poll poll = session.find(Poll.class, id);

    /* Then */
    assertEquals("Test description", poll.getDescription());
  }

  @Test
  @Order(3)
  public void testUpdate() {
    System.out.println("Running [CalendarTest] testUpdate...");
    /* Given */
    Long id = 1L;
    Poll poll = session.find(Poll.class, id);
    poll.setDescription("Updated Description");

    /* When */
    session.beginTransaction();
    session.update(poll);
    session.getTransaction().commit();

    Poll updatedPoll = session.find(Poll.class, id);

    /* Then */
    assertEquals("Updated Description", updatedPoll.getDescription());
  }

  @Test
  @Order(4)
  public void testList() {
    System.out.println("Running [CalendarTest] testList...");
    /* Given */
    String queryString = "from Poll";

    /* When */
    Query<Poll> query = session.createQuery(queryString, Poll.class);
    List<Poll> resultList = query.getResultList();

    /* Then */
    assertFalse(resultList.isEmpty());
  }

  @Test
  @Order(5)
  public void testDelete() {
    System.out.println("Running [CalendarTest] testDelete...");
    /* Given */
    Long id = 1L;
    Poll poll = session.find(Poll.class, id);

    /* When */
    session.beginTransaction();
    session.delete(poll);
    session.getTransaction().commit();
    Poll deletedPoll = session.find(Poll.class, id);

    /* Then */
    assertNull(deletedPoll);
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
