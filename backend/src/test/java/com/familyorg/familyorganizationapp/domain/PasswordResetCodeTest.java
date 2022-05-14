package com.familyorg.familyorganizationapp.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.familyorg.familyorganizationapp.utility.HibernateUtil;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
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
public class PasswordResetCodeTest {
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
    System.out.println("Running [PasswordResetCodeTest] testCreate...");
    /* Given */
    session.beginTransaction();
    User user = new User("Test", "User", "testuser", "password", "testuser@test.com", null);
    Long userId = (Long) session.save(user);
    assertTrue(userId > 0);

    PasswordResetCode code = new PasswordResetCode();
    code.setResetCode(UUID.randomUUID().toString());
    code.setCreated(Timestamp.from(Instant.now()));
    code.setUser(user);

    /* When */
    Long id = (Long) session.save(code);
    session.getTransaction().commit();

    /* Then */
    assertTrue(id > 0);
  }

  @Test
  @Order(2)
  public void testGet() {
    System.out.println("Running [PasswordResetCodeTest] testGet...");
    /* Given */
    Long id = 1L;

    /* When */
    PasswordResetCode code = session.find(PasswordResetCode.class, id);

    /* Then */
    assertNotNull(code.getResetCode());
    assertDoesNotThrow(
        () -> {
          UUID.fromString(code.getResetCode());
        });
  }

  @Test
  @Order(3)
  public void testList() {
    System.out.println("Running [PasswordResetCodeTest] testList...");
    /* Given */
    String queryString = "from PasswordResetCode";

    /* When */
    Query<PasswordResetCode> query = session.createQuery(queryString, PasswordResetCode.class);
    List<PasswordResetCode> resultList = query.getResultList();

    /* Then */
    assertFalse(resultList.isEmpty());
  }

  @Test
  @Order(4)
  public void testDelete() {
    System.out.println("Running [PasswordResetCodeTest] testDelete...");
    /* Given */
    Long id = 1L;
    PasswordResetCode code = session.find(PasswordResetCode.class, id);

    /* When */
    session.beginTransaction();
    session.delete(code);
    session.getTransaction().commit();
    PasswordResetCode deletedCode = session.find(PasswordResetCode.class, id);

    /* Then */
    assertNull(deletedCode);
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
