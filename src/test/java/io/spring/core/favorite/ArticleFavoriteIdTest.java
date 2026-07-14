package io.spring.core.favorite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class ArticleFavoriteIdTest {

  @Test
  public void should_be_equal_to_itself() {
    ArticleFavoriteId id = new ArticleFavoriteId("a1", "u1");
    assertEquals(id, id);
  }

  @Test
  public void should_not_be_equal_to_null() {
    ArticleFavoriteId id = new ArticleFavoriteId("a1", "u1");
    assertNotEquals(id, null);
  }

  @Test
  public void should_not_be_equal_to_different_type() {
    ArticleFavoriteId id = new ArticleFavoriteId("a1", "u1");
    assertNotEquals(id, "some string");
  }

  @Test
  public void should_be_equal_when_same_articleId_and_userId() {
    ArticleFavoriteId id1 = new ArticleFavoriteId("a1", "u1");
    ArticleFavoriteId id2 = new ArticleFavoriteId("a1", "u1");
    assertEquals(id1, id2);
  }

  @Test
  public void should_not_be_equal_when_articleId_differs() {
    ArticleFavoriteId id1 = new ArticleFavoriteId("a1", "u1");
    ArticleFavoriteId id2 = new ArticleFavoriteId("a2", "u1");
    assertNotEquals(id1, id2);
  }

  @Test
  public void should_not_be_equal_when_userId_differs() {
    ArticleFavoriteId id1 = new ArticleFavoriteId("a1", "u1");
    ArticleFavoriteId id2 = new ArticleFavoriteId("a1", "u2");
    assertNotEquals(id1, id2);
  }

  @Test
  void should_have_same_hashCode_for_equal_objects() {
    ArticleFavoriteId id1 = new ArticleFavoriteId("a1", "u1");
    ArticleFavoriteId id2 = new ArticleFavoriteId("a1", "u1");
    assertEquals(id1.hashCode(), id2.hashCode());
    assertEquals(java.util.Objects.hash("a1", "u1"), id1.hashCode());
  }
}
