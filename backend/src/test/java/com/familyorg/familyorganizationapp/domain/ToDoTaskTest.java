package com.familyorg.familyorganizationapp.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.familyorg.familyorganizationapp.utility.HibernateUtil;
import java.sql.Date;
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
public class ToDoTaskTest {
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
    System.out.println("Running [ToDoTaskTest] testCreate...");
    /* Given */
    session.beginTransaction();
    ToDoTask task = new ToDoTask();
    task.setDescription("Test description");
    task.setCreatedDatetime(Timestamp.from(Instant.now()));
    task.setDueDate(Date.valueOf("2022-12-01"));
    task.setNotes("test notes");


    /* When */
    Long id = (Long) session.save(task);
    session.getTransaction().commit();

    /* Then */
    assertTrue(id > 0);
  }

  @Test
  @Order(2)
  public void testGet() {
    System.out.println("Running [ToDoTaskTest] testGet...");
    /* Given */
    Long id = 1L;

    /* When */
    ToDoTask task = session.find(ToDoTask.class, id);

    /* Then */
    assertEquals("Test description", task.getDescription());
    assertNull(task.getCompletedDatetime());
  }

  @Test
  @Order(3)
  public void testUpdate() {
    System.out.println("Running [ToDoTaskTest] testUpdate...");
    /* Given */
    Long id = 1L;
    ToDoTask task = session.find(ToDoTask.class, id);
    task.setCompletedDatetime(Timestamp.from(Instant.now()));

    /* When */
    session.beginTransaction();
    session.update(task);
    session.getTransaction().commit();

    ToDoTask updatedTask = session.find(ToDoTask.class, id);

    /* Then */
    assertNotNull(updatedTask.getCompletedDatetime());
  }

  @Test
  @Order(4)
  public void testList() {
    System.out.println("Running [ToDoTaskTest] testList...");
    /* Given */
    String queryString = "from ToDoTask";

    /* When */
    Query<ToDoTask> query = session.createQuery(queryString, ToDoTask.class);
    List<ToDoTask> resultList = query.getResultList();

    /* Then */
    assertFalse(resultList.isEmpty());
  }

  @Test
  @Order(5)
  public void testDelete() {
    System.out.println("Running [ToDoTaskTest] testDelete...");
    /* Given */
    Long id = 1L;
    ToDoTask task = session.find(ToDoTask.class, id);

    /* When */
    session.beginTransaction();
    session.delete(task);
    session.getTransaction().commit();
    ToDoTask deletedTask = session.find(ToDoTask.class, id);

    /* Then */
    assertNull(deletedTask);
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
