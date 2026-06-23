package io.spring.infrastructure.jpa.repository;

import io.spring.core.article.Tag;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagJpaRepository extends JpaRepository<Tag, String> {

  Optional<Tag> findByName(String name);
}
