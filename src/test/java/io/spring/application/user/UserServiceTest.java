package io.spring.application.user;

import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;
    private static final String DEFAULT_IMAGE = "default.png";

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, DEFAULT_IMAGE, passwordEncoder);
    }

    @Test
    void should_create_user_successfully() {
        // GIVEN
        RegisterParam param = new RegisterParam("email@test.com", "testuser", "password123");
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        // WHEN
        User result = userService.createUser(param);

        // THEN
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("email@test.com");
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getPassword()).isEqualTo("encodedPassword");
        assertThat(result.getBio()).isEqualTo("");
        assertThat(result.getImage()).isEqualTo(DEFAULT_IMAGE);
    }

    @Test
    void should_update_user_successfully() {
        // GIVEN
        User targetUser = new User("old@email.com", "olduser", "oldpass", "", "");
        UpdateUserParam param = UpdateUserParam.builder()
                .email("new@email.com")
                .username("newuser")
                .password("newpass")
                .bio("new bio")
                .image("new.jpg")
                .build();
        UpdateUserCommand command = new UpdateUserCommand(targetUser, param);

        // WHEN
        userService.updateUser(command);

        // THEN
        verify(userRepository).save(targetUser);
        assertThat(targetUser.getEmail()).isEqualTo("new@email.com");
        assertThat(targetUser.getUsername()).isEqualTo("newuser");
        assertThat(targetUser.getPassword()).isEqualTo("newpass");
        assertThat(targetUser.getBio()).isEqualTo("new bio");
        assertThat(targetUser.getImage()).isEqualTo("new.jpg");
    }
}