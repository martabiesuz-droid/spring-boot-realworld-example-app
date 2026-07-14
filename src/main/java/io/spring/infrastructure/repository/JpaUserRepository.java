package io.spring.infrastructure.repository;

import io.spring.core.user.FollowRelation;
import io.spring.core.user.FollowRelationId;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.jpa.repository.FollowRelationJpaRepository;
import io.spring.infrastructure.jpa.repository.UserJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaUserRepository implements UserRepository {

  private final UserJpaRepository userJpaRepository;
  private final FollowRelationJpaRepository followRelationJpaRepository;

  public JpaUserRepository(
      UserJpaRepository userJpaRepository,
      FollowRelationJpaRepository followRelationJpaRepository) {
    this.userJpaRepository = userJpaRepository;
    this.followRelationJpaRepository = followRelationJpaRepository;
  }

  @Override
  public void save(User user) {
    userJpaRepository.saveAndFlush(user);
  }

  @Override
  public Optional<User> findById(String id) {
    return userJpaRepository.findById(id);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return userJpaRepository.findByUsername(username);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return userJpaRepository.findByEmail(email);
  }

  @Override
  public void saveRelation(FollowRelation followRelation) {
    boolean alreadyExists =
        followRelationJpaRepository
            .findByUserIdAndTargetId(followRelation.getUserId(), followRelation.getTargetId())
            .isPresent();
    if (!alreadyExists) {
      followRelationJpaRepository.saveAndFlush(followRelation);
    }
  }

  @Override
  public Optional<FollowRelation> findRelation(String userId, String targetId) {
    return followRelationJpaRepository.findByUserIdAndTargetId(userId, targetId);
  }

  @Override
  public void removeRelation(FollowRelation followRelation) {
    followRelationJpaRepository.deleteById(
        new FollowRelationId(followRelation.getUserId(), followRelation.getTargetId()));
    followRelationJpaRepository.flush();
  }
}
