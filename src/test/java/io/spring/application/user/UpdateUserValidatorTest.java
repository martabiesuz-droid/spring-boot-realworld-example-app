package io.spring.application.user;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UpdateUserValidatorTest {

  @Mock private UserRepository mockRepository;

  @Mock private ConstraintValidatorContext mockContext;

  @Mock private ConstraintValidatorContext.ConstraintViolationBuilder mockBuilder;

  @Mock
  private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext
      mockNodeBuilder;

  private UpdateUserValidator validator;

  @BeforeEach
  void setUp() {
    validator = new UpdateUserValidator();
    ReflectionTestUtils.setField(validator, "userRepository", mockRepository);
  }

  @Test
  void should_return_true_when_data_is_free() {
    // GIVEN
    User targetUser = new User("test@example.com", "testuser", "password", "", "");
    UpdateUserParam param =
        UpdateUserParam.builder().email("new@example.com").username("newuser").build();
    UpdateUserCommand command = new UpdateUserCommand(targetUser, param);

    when(mockRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
    when(mockRepository.findByUsername("newuser")).thenReturn(Optional.empty());

    // WHEN
    boolean result = validator.isValid(command, mockContext);

    // THEN
    assertTrue(result);
    verify(mockContext, never()).disableDefaultConstraintViolation();
    verify(mockContext, never()).buildConstraintViolationWithTemplate(anyString());
  }

  @Test
  void should_return_true_when_data_belongs_to_same_user() {
    // GIVEN
    User targetUser = new User("test@example.com", "testuser", "password", "", "");
    UpdateUserParam param =
        UpdateUserParam.builder().email("test@example.com").username("testuser").build();
    UpdateUserCommand command = new UpdateUserCommand(targetUser, param);

    when(mockRepository.findByEmail("test@example.com")).thenReturn(Optional.of(targetUser));
    when(mockRepository.findByUsername("testuser")).thenReturn(Optional.of(targetUser));

    // WHEN
    boolean result = validator.isValid(command, mockContext);

    // THEN
    assertTrue(result);
    verify(mockContext, never()).disableDefaultConstraintViolation();
    verify(mockContext, never()).buildConstraintViolationWithTemplate(anyString());
  }

  @Test
  void should_return_false_when_email_conflicts_with_other_user() {
    // GIVEN
    when(mockContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(mockBuilder);
    when(mockBuilder.addPropertyNode(anyString())).thenReturn(mockNodeBuilder);
    when(mockNodeBuilder.addConstraintViolation()).thenReturn(mockContext);

    User targetUser = new User("target@example.com", "targetuser", "password", "", "");
    User otherUser = new User("other@example.com", "otheruser", "password", "", "");
    UpdateUserParam param =
        UpdateUserParam.builder().email("other@example.com").username("newusername").build();
    UpdateUserCommand command = new UpdateUserCommand(targetUser, param);

    when(mockRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));
    when(mockRepository.findByUsername("newusername")).thenReturn(Optional.empty());

    // WHEN
    boolean result = validator.isValid(command, mockContext);

    // THEN
    assertFalse(result);
    verify(mockContext).disableDefaultConstraintViolation();
    verify(mockContext).buildConstraintViolationWithTemplate("email already exist");
    verify(mockContext, never()).buildConstraintViolationWithTemplate("username already exist");
  }

  @Test
  void should_return_false_when_username_conflicts_with_other_user() {
    // GIVEN
    when(mockContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(mockBuilder);
    when(mockBuilder.addPropertyNode(anyString())).thenReturn(mockNodeBuilder);
    when(mockNodeBuilder.addConstraintViolation()).thenReturn(mockContext);

    User targetUser = new User("target@example.com", "targetuser", "password", "", "");
    User otherUser = new User("other@example.com", "otheruser", "password", "", "");
    UpdateUserParam param =
        UpdateUserParam.builder().email("newemail@example.com").username("otheruser").build();
    UpdateUserCommand command = new UpdateUserCommand(targetUser, param);

    when(mockRepository.findByEmail("newemail@example.com")).thenReturn(Optional.empty());
    when(mockRepository.findByUsername("otheruser")).thenReturn(Optional.of(otherUser));

    // WHEN
    boolean result = validator.isValid(command, mockContext);

    // THEN
    assertFalse(result);
    verify(mockContext).disableDefaultConstraintViolation();
    verify(mockContext, never()).buildConstraintViolationWithTemplate("email already exist");
    verify(mockContext).buildConstraintViolationWithTemplate("username already exist");
  }

  @Test
  void should_return_false_when_both_email_and_username_conflict_with_other_users() {
    // GIVEN
    when(mockContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(mockBuilder);
    when(mockBuilder.addPropertyNode(anyString())).thenReturn(mockNodeBuilder);
    when(mockNodeBuilder.addConstraintViolation()).thenReturn(mockContext);

    User targetUser = new User("target@example.com", "targetuser", "password", "", "");
    User emailUser = new User("email@example.com", "emailuser", "password", "", "");
    User usernameUser = new User("username@example.com", "usernameuser", "password", "", "");
    UpdateUserParam param =
        UpdateUserParam.builder().email("email@example.com").username("usernameuser").build();
    UpdateUserCommand command = new UpdateUserCommand(targetUser, param);

    when(mockRepository.findByEmail("email@example.com")).thenReturn(Optional.of(emailUser));
    when(mockRepository.findByUsername("usernameuser")).thenReturn(Optional.of(usernameUser));

    // WHEN
    boolean result = validator.isValid(command, mockContext);

    // THEN
    assertFalse(result);
    verify(mockContext).disableDefaultConstraintViolation();
    verify(mockContext).buildConstraintViolationWithTemplate("email already exist");
    verify(mockContext).buildConstraintViolationWithTemplate("username already exist");
  }
}
