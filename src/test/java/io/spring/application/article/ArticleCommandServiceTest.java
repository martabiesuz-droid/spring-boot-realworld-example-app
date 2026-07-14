package io.spring.application.article;

import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ArticleCommandServiceTest {

    @Mock
    private ArticleRepository mockRepository;

    private ArticleCommandService articleCommandService;

    @BeforeEach
    void setUp() {
        articleCommandService = new ArticleCommandService(mockRepository);
    }

@Test
    void should_create_article_successfully() {
        // GIVEN
        NewArticleParam param = NewArticleParam.builder()
                .title("Test Title")
                .description("Test Description")
                .body("Test Body")
                .tagList(Arrays.asList("java", "spring"))
                .build();
        
        User creator = new User("creator@test.com", "creator", "password", "", "");
        String creatorId = creator.getId();

        // WHEN
        Article result = articleCommandService.createArticle(param, creator);

        // THEN
        verify(mockRepository).save(any(Article.class));
        assertNotNull(result);
        assertThat(result.getTitle()).isEqualTo("Test Title");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getBody()).isEqualTo("Test Body");
        assertThat(result.getUserId()).isEqualTo(creatorId);
    }

    @Test
    void should_update_article_successfully() {
        // GIVEN
        List<String> tagList = Arrays.asList("java", "spring");
        Article existingArticle = new Article(
                "Old Title",
                "Old Description", 
                "Old Body",
                tagList,
                "user-123"
        );

        UpdateArticleParam param = new UpdateArticleParam(
                "New Title",
                "New Body",
                "New Description"
        );

        // WHEN
        Article result = articleCommandService.updateArticle(existingArticle, param);

// THEN
        verify(mockRepository).save(existingArticle);
        assertThat(result).isNotNull();
        assertThat(result).isSameAs(existingArticle);
        assertThat(existingArticle.getTitle()).isEqualTo(param.getTitle());
        assertThat(existingArticle.getDescription()).isEqualTo(param.getDescription());
        assertThat(existingArticle.getBody()).isEqualTo(param.getBody());
    }
}