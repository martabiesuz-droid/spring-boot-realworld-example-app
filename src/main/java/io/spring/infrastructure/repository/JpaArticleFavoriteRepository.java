package io.spring.infrastructure.repository;

import io.spring.core.favorite.ArticleFavorite;
import io.spring.core.favorite.ArticleFavoriteId;
import io.spring.core.favorite.ArticleFavoriteRepository;
import io.spring.infrastructure.jpa.repository.ArticleFavoriteJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaArticleFavoriteRepository implements ArticleFavoriteRepository {

  private final ArticleFavoriteJpaRepository articleFavoriteJpaRepository;

  public JpaArticleFavoriteRepository(ArticleFavoriteJpaRepository articleFavoriteJpaRepository) {
    this.articleFavoriteJpaRepository = articleFavoriteJpaRepository;
  }

  @Override
  public void save(ArticleFavorite articleFavorite) {
    boolean alreadyExists =
        articleFavoriteJpaRepository
            .findByArticleIdAndUserId(articleFavorite.getArticleId(), articleFavorite.getUserId())
            .isPresent();
    if (!alreadyExists) {
      articleFavoriteJpaRepository.saveAndFlush(articleFavorite);
    }
  }

  @Override
  public Optional<ArticleFavorite> find(String articleId, String userId) {
    return articleFavoriteJpaRepository.findByArticleIdAndUserId(articleId, userId);
  }

  @Override
  public void remove(ArticleFavorite favorite) {
    articleFavoriteJpaRepository.deleteById(
        new ArticleFavoriteId(favorite.getArticleId(), favorite.getUserId()));
  }
}
