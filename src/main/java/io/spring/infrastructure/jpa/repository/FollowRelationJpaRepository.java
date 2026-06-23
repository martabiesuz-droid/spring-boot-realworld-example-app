package io.spring.infrastructure.jpa.repository;

import io.spring.core.user.FollowRelation;
import io.spring.core.user.FollowRelationId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRelationJpaRepository
    extends JpaRepository<FollowRelation, FollowRelationId> {

  Optional<FollowRelation> findByUserIdAndTargetId(String userId, String targetId);
}
