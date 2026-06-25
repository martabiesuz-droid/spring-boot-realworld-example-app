package io.spring.core.comment;

import io.spring.core.AbstractPersistableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
@SQLDelete(sql = "UPDATE comments SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Comment extends AbstractPersistableEntity {

  @Id private String id;
  private String body;

  @Column(name = "article_id")
  private String articleId;

  @Column(name = "user_id")
  private String userId;

  @Column(name = "created_at")
  private Instant createdAt;

  @Column(name = "is_deleted")
  private boolean deleted = false;

  public Comment(String body, String userId, String articleId) {
    this.id = UUID.randomUUID().toString();
    this.body = body;
    this.userId = userId;
    this.articleId = articleId;
    this.createdAt = Instant.now();
  }
}
