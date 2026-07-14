package io.spring.infrastructure.jpa.repository;

import io.spring.core.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<Comment, String> {}
