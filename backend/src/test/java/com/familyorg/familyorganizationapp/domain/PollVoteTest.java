package com.familyorg.familyorganizationapp.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.familyorg.familyorganizationapp.domain.id.VoteId;
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
public class PollVoteTest {
  private static SessionFactory sessionFactory;
  private Session session;

  private static User user;
  private static Poll poll;

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
    System.out.println("Running [PollVoteTest] testCreate...");
    /* Given */
    session.beginTransaction();
    poll = new Poll();
    poll.setDescription("Test description");
    poll.setCloseDateTime(Timestamp.from(Instant.now()));
    poll.setCreatedDatetime(Timestamp.from(Instant.now()));
    poll.setTimezone("America/Chicago");
    Long pollId = (Long) session.save(poll);
    assertTrue(pollId > 0);
    user = new User("Test", "User", "testuser", "password", "testuser@test.com", null);
    Long userId = (Long) session.save(user);
    assertTrue(userId > 0);
    PollOption option = new PollOption();
    option.setValue("test value");
    Long optionId = (Long) session.save(option);
    assertTrue(optionId > 0);

    PollVote vote = new PollVote();
    vote.setPoll(poll);
    vote.setUser(user);

    /* When */
    VoteId id = (VoteId) session.save(vote);
    session.getTransaction().commit();

    /* Then */
    assertNotNull(id);
    assertEquals(pollId, id.getPollId());
    assertEquals(userId, id.getUserId());
  }

  @Test
  @Order(2)
  public void testGet() {
    System.out.println("Running [PollVoteTest] testGet...");
    /* Given */
    VoteId id = new VoteId(poll.getId(), user.getId());

    /* When */
    PollVote vote = session.find(PollVote.class, id);

    /* Then */
    assertNull(vote.getVote());
    assertEquals(user.getId(), vote.getUser().getId());
    assertEquals(poll.getId(), vote.getPoll().getId());
  }

  @Test
  @Order(3)
  public void testUpdate() {
    System.out.println("Running [PollVoteTest] testUpdate...");
    /* Given */

    VoteId id = new VoteId(poll.getId(), user.getId());
    PollVote vote = session.find(PollVote.class, id);
    vote.setVote(session.find(PollOption.class, 1L));

    /* When */
    session.beginTransaction();
    session.update(vote);
    session.getTransaction().commit();

    PollVote updatedVote = session.find(PollVote.class, id);

    /* Then */
    assertNotNull(updatedVote.getVote());
  }

  @Test
  @Order(4)
  public void testList() {
    System.out.println("Running [PollVoteTest] testList...");
    /* Given */
    String queryString = "from PollVote";

    /* When */
    Query<PollVote> query = session.createQuery(queryString, PollVote.class);
    List<PollVote> resultList = query.getResultList();

    /* Then */
    assertFalse(resultList.isEmpty());
  }

  @Test
  @Order(5)
  public void testDelete() {
    System.out.println("Running [PollVoteTest] testDelete...");
    /* Given */
    VoteId id = new VoteId(poll.getId(), user.getId());
    PollVote vote = session.find(PollVote.class, id);

    /* When */
    session.beginTransaction();
    session.delete(vote);
    session.getTransaction().commit();
    PollVote deletedVote = session.find(PollVote.class, id);

    /* Then */
    assertNull(deletedVote);
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
