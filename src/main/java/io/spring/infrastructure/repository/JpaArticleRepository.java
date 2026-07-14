package io.spring.infrastructure.repository;

import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.article.Tag;
import io.spring.infrastructure.jpa.repository.ArticleJpaRepository;
import io.spring.infrastructure.jpa.repository.TagJpaRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JpaArticleRepository implements ArticleRepository {

  private final ArticleJpaRepository articleJpaRepository;
  private final TagJpaRepository tagJpaRepository;

  public JpaArticleRepository(
      ArticleJpaRepository articleJpaRepository, TagJpaRepository tagJpaRepository) {
    this.articleJpaRepository = articleJpaRepository;
    this.tagJpaRepository = tagJpaRepository;
  }

  @Override
  @Transactional
  public void save(Article article) {
    attachManagedTags(article);
    articleJpaRepository.saveAndFlush(article);
  }

  private void attachManagedTags(Article article) {
    List<Tag> originalTags = article.getTags();
    List<Tag> managedTags = new ArrayList<>();
    for (Tag tag : originalTags) {
      Tag managedTag =
          tagJpaRepository.findByName(tag.getName()).orElseGet(() -> tagJpaRepository.save(tag));
      managedTags.add(managedTag);
    }
    originalTags.clear();
    originalTags.addAll(managedTags);
  }

  @Override
  public Optional<Article> findById(String id) {
    return articleJpaRepository.findById(id);
  }

  @Override
  public Optional<Article> findBySlug(String slug) {
    return articleJpaRepository.findBySlug(slug);
  }

  @Override
  public void remove(Article article) {
    articleJpaRepository.deleteById(article.getId());
  }
}
