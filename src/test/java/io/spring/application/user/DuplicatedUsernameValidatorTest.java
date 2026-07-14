package io.spring.application.user;

import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DuplicatedUsernameValidatorTest {

    @Mock
    private UserRepository mockRepository;

    private DuplicatedUsernameValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DuplicatedUsernameValidator();
        ReflectionTestUtils.setField(validator, "userRepository", mockRepository);
    }

    @Test
    void should_return_true_when_username_is_null() {
        // WHEN
        boolean result = validator.isValid(null, null);

        // THEN
        assertTrue(result);
    }

    @Test
    void should_return_true_when_username_is_empty() {
        // WHEN
        boolean result = validator.isValid("", null);

        // THEN
        assertTrue(result);
    }

    @Test
    void should_return_true_when_username_is_not_taken() {
        // GIVEN
        when(mockRepository.findByUsername("freeuser")).thenReturn(Optional.empty());

        // WHEN
        boolean result = validator.isValid("freeuser", null);

        // THEN
        assertTrue(result);
    }

    @Test
    void should_return_false_when_username_is_already_taken() {
        // GIVEN
        User user = new User("email@test.com", "username", "password", "", "");
        when(mockRepository.findByUsername("takenuser")).thenReturn(Optional.of(user));

        // WHEN
        boolean result = validator.isValid("takenuser", null);

        // THEN
        assertFalse(result);
    }
}