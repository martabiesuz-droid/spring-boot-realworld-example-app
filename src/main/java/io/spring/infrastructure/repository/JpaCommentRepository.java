package io.spring.infrastructure.repository;

import io.spring.core.comment.Comment;
import io.spring.core.comment.CommentRepository;
import io.spring.infrastructure.jpa.repository.CommentJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaCommentRepository implements CommentRepository {

  private final CommentJpaRepository commentJpaRepository;

  public JpaCommentRepository(CommentJpaRepository commentJpaRepository) {
    this.commentJpaRepository = commentJpaRepository;
  }

  @Override
  public void save(Comment comment) {
    commentJpaRepository.saveAndFlush(comment);
  }

  @Override
  public Optional<Comment> findById(String articleId, String id) {
    return commentJpaRepository.findById(id);
  }

  @Override
  public void remove(Comment comment) {
    commentJpaRepository.deleteById(comment.getId());
  }
}
