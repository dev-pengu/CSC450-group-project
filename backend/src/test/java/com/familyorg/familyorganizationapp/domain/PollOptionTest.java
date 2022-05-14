package com.familyorg.familyorganizationapp.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.familyorg.familyorganizationapp.utility.HibernateUtil;
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
public class PollOptionTest {
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
    System.out.println("Running [PollOptionTest] testCreate...");
    /* Given */
    session.beginTransaction();
    PollOption option = new PollOption();
    option.setValue("test value");

    /* When */
    Long id = (Long) session.save(option);
    session.getTransaction().commit();

    /* Then */
    assertTrue(id > 0);
  }

  @Test
  @Order(2)
  public void testGet() {
    System.out.println("Running [PollOptionTest] testGet...");
    /* Given */
    Long id = 1L;

    /* When */
    PollOption option = session.find(PollOption.class, id);

    /* Then */
    assertEquals("test value", option.getValue());
  }

  @Test
  @Order(3)
  public void testUpdate() {
    System.out.println("Running [PollOptionTest] testUpdate...");
    /* Given */
    Long id = 1L;
    PollOption option = session.find(PollOption.class, id);
    option.setValue("updated value");

    /* When */
    session.beginTransaction();
    session.update(option);
    session.getTransaction().commit();

    PollOption updatedOption = session.find(PollOption.class, id);

    /* Then */
    assertEquals("updated value", updatedOption.getValue());
  }

  @Test
  @Order(4)
  public void testList() {
    System.out.println("Running [PollOptionTest] testList...");
    /* Given */
    String queryString = "from PollOption";

    /* When */
    Query<PollOption> query = session.createQuery(queryString, PollOption.class);
    List<PollOption> resultList = query.getResultList();

    /* Then */
    assertFalse(resultList.isEmpty());
  }

  @Test
  @Order(5)
  public void testDelete() {
    System.out.println("Running [PollOptionTest] testDelete...");
    /* Given */
    Long id = 1L;
    PollOption option = session.find(PollOption.class, id);

    /* When */
    session.beginTransaction();
    session.delete(option);
    session.getTransaction().commit();
    PollOption deletedOption = session.find(PollOption.class, id);

    /* Then */
    assertNull(deletedOption);
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
