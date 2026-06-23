package io.spring.core.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "follows")
@IdClass(FollowRelationId.class)
@NoArgsConstructor
@Data
public class FollowRelation {

  @Id
  @Column(name = "user_id")
  private String userId;

  @Id
  @Column(name = "follow_id")
  private String targetId;

  public FollowRelation(String userId, String targetId) {
    this.userId = userId;
    this.targetId = targetId;
  }
}
