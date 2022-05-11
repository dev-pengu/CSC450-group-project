package com.familyorg.familyorganizationapp.domain;

import com.familyorg.familyorganizationapp.domain.id.MemberInviteId;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MemberInviteTest {
  private static SessionFactory sessionFactory;
  private Session session;

  private static Family family;
  private static MemberInviteId targetId;

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
    Long familyId = (Long) session.save(family);
    assertTrue(familyId > 0);

    MemberInvite memberInvite = new MemberInvite(family, "testuser@test.com", Role.ADULT);


    /* When */
    MemberInviteId id = (MemberInviteId) session.save(memberInvite);
    session.getTransaction().commit();

    /* Then */
    assertTrue(id.getFamily() == 1l);
    assertTrue(id.getInviteCode() != null);
    assertTrue(id.getUserEmail().equals(memberInvite.getUserEmail()));
    targetId = id;
  }

  // No update test is needed, updates will not be performed on this DAO

  @Test
  @Order(2)
  public void testGet() {
    System.out.println("Running [MemberInviteTest] testGet...");
    /* Given */
    assertNotNull(targetId);

    /* When */
    MemberInvite memberInvite = session.find(MemberInvite.class, targetId);

    /* Then */
    assertEquals(Role.ADULT, memberInvite.getRole());
    assertEquals("testuser@test.com", memberInvite.getUserEmail());
    assertEquals(1l, memberInvite.getFamilyId());
    assertTrue(memberInvite.getInviteCodeObj().getInviteCodeString().contains("OTU"));
    assertNotNull(memberInvite.getCreatedAt());
  }

  @Test
  @Order(3)
  public void testList() {
    System.out.println("Running [MemberInviteTest] testList...");
    /* Given */
    String queryString = "from MemberInvite";

    /* When */
    Query<MemberInvite> query = session.createQuery(queryString, MemberInvite.class);
    List<MemberInvite> resultList = query.getResultList();

    /* Then */
    assertFalse(resultList.isEmpty());
  }

  @Test
  @Order(4)
  public void testDelete() {
    System.out.println("Running [MemberInviteTest] testDelete...");
    /* Given */
    assertNotNull(targetId);
    MemberInvite invite = session.find(MemberInvite.class, targetId);

    /* When */
    session.beginTransaction();
    session.delete(invite);
    session.getTransaction().commit();
    MemberInvite deletedInvite = session.find(MemberInvite.class, targetId);

    /* Then */
    assertNull(deletedInvite);
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
