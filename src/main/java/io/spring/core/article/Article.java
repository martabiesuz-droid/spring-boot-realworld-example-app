package io.spring.core.article;

import static java.util.stream.Collectors.toList;

import io.spring.Util;
import io.spring.core.AbstractPersistableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "articles")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@SQLDelete(sql = "UPDATE articles SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Article extends AbstractPersistableEntity {

  @Column(name = "user_id")
  private String userId;

  @Id private String id;
  private String slug;
  private String title;
  private String description;
  private String body;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "article_tags",
      joinColumns = @JoinColumn(name = "article_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private List<Tag> tags;

  @Column(name = "created_at")
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;

  @Column(name = "is_deleted")
  private boolean deleted = false;

  public Article(
      String title, String description, String body, List<String> tagList, String userId) {
    this(title, description, body, tagList, userId, Instant.now());
  }

  public Article(
      String title,
      String description,
      String body,
      List<String> tagList,
      String userId,
      Instant createdAt) {
    this.id = UUID.randomUUID().toString();
    this.slug = toSlug(title);
    this.title = title;
    this.description = description;
    this.body = body;
    this.tags = new HashSet<>(tagList).stream().map(Tag::new).collect(toList());
    this.userId = userId;
    this.createdAt = createdAt;
    this.updatedAt = createdAt;
  }

  public void update(String title, String description, String body) {
    if (!Util.isEmpty(title)) {
      this.title = title;
      this.slug = toSlug(title);
      this.updatedAt = Instant.now();
    }
    if (!Util.isEmpty(description)) {
      this.description = description;
      this.updatedAt = Instant.now();
    }
    if (!Util.isEmpty(body)) {
      this.body = body;
      this.updatedAt = Instant.now();
    }
  }

  public static String toSlug(String title) {
    return title.toLowerCase().replaceAll("[&\\uFE30-\\uFFA0'\"\\s?,.]+", "-");
  }
}
