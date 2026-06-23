package io.spring.infrastructure.jpa.repository;

import io.spring.core.favorite.ArticleFavorite;
import io.spring.core.favorite.ArticleFavoriteId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleFavoriteJpaRepository
    extends JpaRepository<ArticleFavorite, ArticleFavoriteId> {

  Optional<ArticleFavorite> findByArticleIdAndUserId(String articleId, String userId);
}
