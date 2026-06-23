package io.spring.core.favorite;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "article_favorites")
@IdClass(ArticleFavoriteId.class)
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class ArticleFavorite {

  @Id
  @Column(name = "article_id")
  private String articleId;

  @Id
  @Column(name = "user_id")
  private String userId;

  public ArticleFavorite(String articleId, String userId) {
    this.articleId = articleId;
    this.userId = userId;
  }
}
