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
public class ShoppingListItemTest {
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
    System.out.println("Running [ShoppingListItemTest] testCreate...");
    /* Given */
    session.beginTransaction();
    ShoppingListItem item = new ShoppingListItem();
    item.setDescription("Test description");
    item.setNotes("notes");
    item.setUnit("ea");
    item.setAmount(4D);

    /* When */
    Long id = (Long) session.save(item);
    session.getTransaction().commit();

    /* Then */
    assertTrue(id > 0);
  }

  @Test
  @Order(2)
  public void testGet() {
    System.out.println("Running [ShoppingListItemTest] testGet...");
    /* Given */
    Long id = 1L;

    /* When */
    ShoppingListItem item = session.find(ShoppingListItem.class, id);

    /* Then */
    assertEquals("Test description", item.getDescription());
    assertEquals("notes", item.getNotes());
    assertEquals("ea", item.getUnit());
    assertEquals(4D, item.getAmount());
  }

  @Test
  @Order(3)
  public void testUpdate() {
    System.out.println("Running [ShoppingListItemTest] testUpdate...");
    /* Given */
    Long id = 1L;
    ShoppingListItem item = session.find(ShoppingListItem.class, id);
    item.setAmount(6D);

    /* When */
    session.beginTransaction();
    session.update(item);
    session.getTransaction().commit();

    ShoppingListItem updatedItem = session.find(ShoppingListItem.class, id);

    /* Then */
    assertEquals(6D, updatedItem.getAmount());
  }

  @Test
  @Order(4)
  public void testList() {
    System.out.println("Running [ShoppingListItemTest] testList...");
    /* Given */
    String queryString = "from ShoppingListItem";

    /* When */
    Query<ShoppingListItem> query = session.createQuery(queryString, ShoppingListItem.class);
    List<ShoppingListItem> resultList = query.getResultList();

    /* Then */
    assertFalse(resultList.isEmpty());
  }

  @Test
  @Order(5)
  public void testDelete() {
    System.out.println("Running [ShoppingListItemTest] testDelete...");
    /* Given */
    Long id = 1L;
    ShoppingListItem item = session.find(ShoppingListItem.class, id);

    /* When */
    session.beginTransaction();
    session.delete(item);
    session.getTransaction().commit();
    ShoppingListItem deletedItem = session.find(ShoppingListItem.class, id);

    /* Then */
    assertNull(deletedItem);
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
