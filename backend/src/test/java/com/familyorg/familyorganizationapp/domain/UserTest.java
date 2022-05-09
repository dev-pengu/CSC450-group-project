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
public class UserTest {
    private static SessionFactory sessionFactory;
    private Session session;
    private static Long testUserId;

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
        System.out.println("Running [UserTest] testCreate...");
        /* Given */
        session.beginTransaction();
        User user = new User("Test", "User","testuser", "password", "testuser@test.com", null);

        /* When */
        Long id = (Long) session.save(user);
        testUserId = id;
        session.getTransaction().commit();

        /* Then */
        assertTrue(id > 0);
    }

    @Test
    @Order(2)
    public void testUpdate() {
        System.out.println("Running [UserTest] testUpdate...");
        /* Given */
        Long id = testUserId != null ? testUserId : 1l;
        User user = new User(id, "Test", "User","testuser", "newpassword", "testuser@test.com", null);

        /* When */
        session.beginTransaction();
        session.update(user);
        session.getTransaction().commit();

        User updated = session.find(User.class, id);

        /* Then */
        assertEquals("newpassword", updated.getPassword());
    }

    @Test
    @Order(3)
    public void testGet() {
        System.out.println("Running [UserTest] testGet...");
        /* Given */
        Long id = testUserId != null ? testUserId : 1l;

        /* When */
        User user = session.find(User.class, id);

        /* Then */
        assertEquals("Test", user.getFirstName());
        assertEquals("User", user.getLastName());
        assertEquals("testuser", user.getUsername());
        assertEquals("testuser@test.com", user.getEmail());
    }

    @Test
    @Order(4)
    public void testList() {
        System.out.println("Running [UserTest] testList...");
        /* Given */
        String queryString = "from User";

        /* When */
        Query<User> query = session.createQuery(queryString, User.class);
        List<User> resultList = query.getResultList();

        /* Then */
        assertFalse(resultList.isEmpty());
    }

    @Test
    @Order(5)
    public void testDelete() {
        System.out.println("Running [UserTest] testDelete...");
        /* Given */
        Long id = testUserId != null ? testUserId : 1l;
        User user = session.find(User.class, id);

        /* When */
        session.beginTransaction();
        session.delete(user);
        session.getTransaction().commit();
        User deletedUser = session.find(User.class, id);

        /* Then */
        assertNull(deletedUser);
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
