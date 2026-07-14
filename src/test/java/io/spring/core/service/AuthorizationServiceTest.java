package io.spring.core.service;

import io.spring.core.article.Article;
import io.spring.core.comment.Comment;
import io.spring.core.user.User;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AuthorizationServiceTest {

  @Test
  public void should_return_true_when_user_is_article_author() {
    User user = new User("email@test.com", "username", "123", "", "");
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    
    boolean result = AuthorizationService.canWriteArticle(user, article);
    
    Assertions.assertTrue(result);
  }

  @Test
  public void should_return_false_when_user_is_not_article_author() {
    User user = new User("email@test.com", "username", "123", "", "");
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), "fixed-different-id");
    
    boolean result = AuthorizationService.canWriteArticle(user, article);
    
    Assertions.assertFalse(result);
  }

  @Test
  public void should_return_true_when_user_is_article_author_but_not_comment_author() {
    User user = new User("email@test.com", "username", "123", "", "");
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    Comment comment = new Comment("content", "different-comment-id", article.getId());
    
    boolean result = AuthorizationService.canWriteComment(user, article, comment);
    
    Assertions.assertTrue(result);
  }

  @Test
  public void should_return_true_when_user_is_comment_author_but_not_article_author() {
    User user = new User("email@test.com", "username", "123", "", "");
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), "different-article-id");
    Comment comment = new Comment("content", user.getId(), article.getId());
    
    boolean result = AuthorizationService.canWriteComment(user, article, comment);
    
    Assertions.assertTrue(result);
  }

  @Test
  public void should_return_false_when_user_is_neither_article_author_nor_comment_author() {
    User user = new User("email@test.com", "username", "123", "", "");
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), "article-author-id");
    Comment comment = new Comment("content", "comment-author-id", article.getId());
    
    boolean result = AuthorizationService.canWriteComment(user, article, comment);
    
    Assertions.assertFalse(result);
  }

  @Test
  public void should_return_true_when_user_is_both_article_and_comment_author() {
    User user = new User("email@test.com", "username", "123", "", "");
    Article article = new Article("title", "desc", "body", Arrays.asList("java"), user.getId());
    Comment comment = new Comment("content", user.getId(), article.getId());
    
    boolean result = AuthorizationService.canWriteComment(user, article, comment);
    
    Assertions.assertTrue(result);
  }
}