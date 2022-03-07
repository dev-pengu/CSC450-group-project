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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FamilyMemberTest {
    private static SessionFactory sessionFactory;
    private Session session;

    private static User user;
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
        System.out.println("Running [FamilyMemberTest] testCreate...");
        /* Given */
        session.beginTransaction();
        family = new Family("Test Name", "000000", "America/Chicago", null, null);
        user = new User("Test", "User", "testuser", "password", "testuser@test.com", null);
        FamilyMembers familyMembers = new FamilyMembers(user, family, Role.CHILD, "fffff");
        Long familyId = (Long) session.save(family);
        Long userId = (Long) session.save(user);
        assertTrue(familyId > 0);
        assertTrue(userId > 0);

        /* When */
        FamilyMemberId id = (FamilyMemberId) session.save(familyMembers);
        session.getTransaction().commit();

        /* Then */
        assertNotNull(id);
        assertEquals(familyId, id.family);
        assertEquals(userId, id.user);
    }

    @Test
    @Order(3)
    public void testUpdate() {
        System.out.println("Running [FamilyMemberTest] testUpdate...");
        /* Given */
        FamilyMembers familyMembers = new FamilyMembers(user, family, Role.ADULT, "ffffff");

        /* When */
        session.beginTransaction();
        session.update(familyMembers);
        session.getTransaction().commit();

        FamilyMembers updated = session.find(FamilyMembers.class, new FamilyMemberId(user.getId(), family.getId()));

        /* Then */
        assertEquals(Role.ADULT, updated.getRole());
    }

    @Test
    @Order(2)
    public void testGet() {
        System.out.println("Running [FamilyMemberTest] testGet...");
        /* Given */
        FamilyMemberId id = new FamilyMemberId(user.getId(), family.getId());

        /* When */
        FamilyMembers familyMembers = session.get(FamilyMembers.class, id);

        /* Then */
        assertEquals(user, familyMembers.getUser());
        assertEquals(family, familyMembers.getFamily());
        assertEquals(Role.CHILD, familyMembers.getRole());
        assertEquals("fffff", familyMembers.getEventColor());
    }

    @Test
    @Order(4)
    public void testList() {
        System.out.println("Running [FamilyMemberTest] testList...");
        /* Given */
        String queryString = "from FamilyMembers";

        /* When */
        Query<FamilyMembers> query = session.createQuery(queryString, FamilyMembers.class);
        List<FamilyMembers> resultList = query.getResultList();

        /* Then */
        assertFalse(resultList.isEmpty());
    }

    @Test
    @Order(5)
    public void testDelete() {
        System.out.println("Running [FamilyTest] testDelete...");
        /* Given */
        FamilyMemberId id = new FamilyMemberId(user.getId(), family.getId());
        FamilyMembers familyMembers = session.find(FamilyMembers.class, id);

        /* When */
        session.beginTransaction();
        session.delete(familyMembers);
        session.getTransaction().commit();
        FamilyMembers deletedFamilyMembers = session.find(FamilyMembers.class, id);


        /* Then */
        assertNull(deletedFamilyMembers);
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
