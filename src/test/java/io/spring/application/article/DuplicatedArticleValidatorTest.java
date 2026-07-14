package io.spring.application.article;

import io.spring.application.ArticleQueryService;
import io.spring.application.data.ArticleData;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DuplicatedArticleValidatorTest {

    @Mock
    private ArticleQueryService mockService;

    private DuplicatedArticleValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DuplicatedArticleValidator();
        ReflectionTestUtils.setField(validator, "articleQueryService", mockService);
    }

    @Test
    void should_return_true_when_slug_is_not_taken() {
        // GIVEN
        when(mockService.findBySlug(any(), isNull())).thenReturn(Optional.empty());

        // WHEN
        boolean result = validator.isValid("Some Title", null);

        // THEN
        assertTrue(result);
    }

    @Test
    void should_return_false_when_slug_is_already_taken() {
        // GIVEN
        ArticleData existingArticleData = new ArticleData();
        when(mockService.findBySlug(any(), isNull())).thenReturn(Optional.of(existingArticleData));

        // WHEN
        boolean result = validator.isValid("Some Title", null);

        // THEN
        assertFalse(result);
    }
}