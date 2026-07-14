package io.spring.infrastructure.jpa.repository;

import io.spring.core.article.Article;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleJpaRepository extends JpaRepository<Article, String> {

  Optional<Article> findBySlug(String slug);
}
