package com.familyorg.familyorganizationapp.domain;

import com.familyorg.familyorganizationapp.util.HibernateUtil;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MemberInviteTest {
  private static SessionFactory sessionFactory;
  private Session session;

  private static Family family;

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
    System.out.println("Running [MemberInviteTest] testCreate...");
    /* Given */
    session.beginTransaction();
    family = new Family("Test Name", "000000", "America/Chicago", null, null);

    MemberInvite memberInvite = new MemberInvite(family, "testuser@test.com", Role.ADULT);
    Family family = new Family("Test Family", "000000", "America/Chicago", null, null);
    Long familyId = (Long) session.save(family);
    assertTrue(familyId > 0);

    /* When */
    Long id = (Long) session.save(memberInvite);
    session.getTransaction().commit();

    /* Then */
    assertTrue(id > 0);
  }

  // No update test is needed, updates will not be performed on this DAO

  @Test
  @Order(2)
  public void testGet() {
    System.out.println("Running [MemberInviteTest] testGet...");
    /* Given */
    Long id = 1l;

    /* When */
    MemberInvite memberInvite = session.find(MemberInvite.class, id);

    /* Then */
    assertEquals("Test Family", family.getName());
  }

  @Test
  @Order(3)
  public void testList() {
    System.out.println("Running [MemberInviteTest] testList...");
    /* Given */
    String queryString = "from Family";

    /* When */
    Query<Family> query = session.createQuery(queryString, Family.class);
    List<Family> resultList = query.getResultList();

    /* Then */
    assertFalse(resultList.isEmpty());
  }

  @Test
  @Order(4)
  public void testDelete() {
    System.out.println("Running [MemberInviteTest] testDelete...");
    /* Given */
    Long id = 1l;
    Family family = session.find(Family.class, id);

    /* When */
    session.beginTransaction();
    session.delete(family);
    session.getTransaction().commit();
    Family deletedFamily = session.find(Family.class, id);

    /* Then */
    assertNull(deletedFamily);
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
