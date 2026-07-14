package io.spring.core.user;

import java.io.Serializable;
import java.util.Objects;

public class FollowRelationId implements Serializable {

  private String userId;
  private String targetId;

  public FollowRelationId() {}

  public FollowRelationId(String userId, String targetId) {
    this.userId = userId;
    this.targetId = targetId;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (!(other instanceof FollowRelationId that)) return false;
    return Objects.equals(userId, that.userId) && Objects.equals(targetId, that.targetId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, targetId);
  }
}
