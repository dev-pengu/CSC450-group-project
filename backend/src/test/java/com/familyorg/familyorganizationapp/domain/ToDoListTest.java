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
public class ToDoListTest {
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
    System.out.println("Running [ToDoListTest] testCreate...");
    /* Given */
    session.beginTransaction();
    ToDoList list = new ToDoList();
    list.setDescription("Test description");
    list.setCreatedDatetime(Timestamp.from(Instant.now()));
    list.setDefaultList(false);

    /* When */
    Long id = (Long) session.save(list);
    session.getTransaction().commit();

    /* Then */
    assertTrue(id > 0);
  }

  @Test
  @Order(2)
  public void testGet() {
    System.out.println("Running [ToDoListTest] testGet...");
    /* Given */
    Long id = 1L;

    /* When */
    ToDoList list = session.find(ToDoList.class, id);

    /* Then */
    assertEquals("Test description", list.getDescription());
    assertEquals(false, list.isDefault());
  }

  @Test
  @Order(3)
  public void testUpdate() {
    System.out.println("Running [ToDoListTest] testUpdate...");
    /* Given */
    Long id = 1L;
    ToDoList list = session.find(ToDoList.class, id);
    list.setDescription("Updated Description");

    /* When */
    session.beginTransaction();
    session.update(list);
    session.getTransaction().commit();

    ToDoList updatedList = session.find(ToDoList.class, id);

    /* Then */
    assertEquals("Updated Description", updatedList.getDescription());
  }

  @Test
  @Order(4)
  public void testList() {
    System.out.println("Running [ToDoListTest] testList...");
    /* Given */
    String queryString = "from ToDoList";

    /* When */
    Query<ToDoList> query = session.createQuery(queryString, ToDoList.class);
    List<ToDoList> resultList = query.getResultList();

    /* Then */
    assertFalse(resultList.isEmpty());
  }

  @Test
  @Order(5)
  public void testDelete() {
    System.out.println("Running [ToDoListTest] testDelete...");
    /* Given */
    Long id = 1L;
    ToDoList list = session.find(ToDoList.class, id);

    /* When */
    session.beginTransaction();
    session.delete(list);
    session.getTransaction().commit();
    ToDoList deletedList = session.find(ToDoList.class, id);

    /* Then */
    assertNull(deletedList);
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
