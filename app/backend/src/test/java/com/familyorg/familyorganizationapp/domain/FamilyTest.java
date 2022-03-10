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

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FamilyTest {
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
        System.out.println("Running [FamilyTest] testCreate...");
        /* Given */
        session.beginTransaction();
        Family family = new Family("Test Family", "000000", "America/Chicago", null, null);

        /* When */
        Long id = (Long) session.save(family);
        session.getTransaction().commit();

        /* Then */
        assertTrue(id > 0);
    }

    @Test
    @Order(2)
    public void testUpdate() {
        System.out.println("Running [FamilyTest] testUpdate...");
        /* Given */
        Long id = 1l;
        Family family = new Family(id, "Test Family", "ffffff", "America/Chicago", null, null);

        /* When */
        session.beginTransaction();
        session.update(family);
        session.getTransaction().commit();

        Family updatedFamily = session.find(Family.class, 1l);

        /* Then */
        assertEquals("ffffff", updatedFamily.getEventColor());
    }

    @Test
    @Order(3)
    public void testAddInviteCode() {
        System.out.println("Running [FamilyTest] testAddInviteCode...");
        /* Given */
        InviteCode inviteCode = new InviteCode(true);
        Long id = 1l;
        Family family = session.find(Family.class, id);
        assertEquals(1l, family.getId());

        /* When */
        family.setInviteCode(inviteCode);
        session.beginTransaction();
        session.update(family);
        session.getTransaction().commit();

        Family updatedFamily = session.find(Family.class, id);

        /* Then */
        assertEquals(inviteCode.toString(), updatedFamily.getInviteCode());
    }

    @Test
    @Order(3)
    public void testGet() {
        System.out.println("Running [FamilyTest] testGet...");
        /* Given */
        Long id = 1l;

        /* When */
        Family family = session.find(Family.class, id);

        /* Then */
        assertEquals("Test Family", family.getName());
    }

    @Test
    @Order(4)
    public void testList() {
        System.out.println("Running [FamilyTest] testList...");
        /* Given */
        String queryString = "from Family";

        /* When */
        Query<Family> query = session.createQuery(queryString, Family.class);
        List<Family> resultList = query.getResultList();

        /* Then */
        assertFalse(resultList.isEmpty());
    }

    @Test
    @Order(5)
    public void testDelete() {
        System.out.println("Running [FamilyTest] testDelete...");
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
