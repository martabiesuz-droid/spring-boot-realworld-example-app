package io.spring.core.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class FollowRelationIdTest {

  @Test
  public void should_be_equal_to_itself() {
    FollowRelationId id = new FollowRelationId("u1", "t1");
    assertEquals(id, id);
  }

  @Test
  public void should_not_be_equal_to_null() {
    FollowRelationId id = new FollowRelationId("u1", "t1");
    assertNotEquals(id, null);
  }

  @Test
  public void should_not_be_equal_to_different_type() {
    FollowRelationId id = new FollowRelationId("u1", "t1");
    assertNotEquals(id, "some string");
  }

  @Test
  public void should_be_equal_when_same_userId_and_targetId() {
    FollowRelationId id1 = new FollowRelationId("u1", "t1");
    FollowRelationId id2 = new FollowRelationId("u1", "t1");
    assertEquals(id1, id2);
  }

  @Test
  public void should_not_be_equal_when_userId_differs() {
    FollowRelationId id1 = new FollowRelationId("u1", "t1");
    FollowRelationId id2 = new FollowRelationId("u2", "t1");
    assertNotEquals(id1, id2);
  }

  @Test
  public void should_not_be_equal_when_targetId_differs() {
    FollowRelationId id1 = new FollowRelationId("u1", "t1");
    FollowRelationId id2 = new FollowRelationId("u1", "t2");
    assertNotEquals(id1, id2);
  }

  @Test
  void should_have_same_hashCode_for_equal_objects() {
    FollowRelationId id1 = new FollowRelationId("u1", "t1");
    FollowRelationId id2 = new FollowRelationId("u1", "t1");
    assertEquals(id1.hashCode(), id2.hashCode());
    assertEquals(java.util.Objects.hash("u1", "t1"), id1.hashCode());
  }
}
