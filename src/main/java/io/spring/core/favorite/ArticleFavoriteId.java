package io.spring.core.favorite;

import java.io.Serializable;
import java.util.Objects;

public class ArticleFavoriteId implements Serializable {

  private String articleId;
  private String userId;

  public ArticleFavoriteId() {}

  public ArticleFavoriteId(String articleId, String userId) {
    this.articleId = articleId;
    this.userId = userId;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (!(other instanceof ArticleFavoriteId that)) return false;
    return Objects.equals(articleId, that.articleId) && Objects.equals(userId, that.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(articleId, userId);
  }
}
