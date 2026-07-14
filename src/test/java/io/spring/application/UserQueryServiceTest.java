package io.spring.application;

import io.spring.application.data.UserData;
import io.spring.infrastructure.mybatis.readservice.UserReadService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserQueryServiceTest {

    @Mock
    private UserReadService mockUserReadService;

    @InjectMocks
    private UserQueryService userQueryService;

    @Test
    public void should_return_userData_when_found() {
        // Arrange
        UserData expectedUserData = new UserData("123", "test@example.com", "testuser", "test bio", "test.jpg");
        when(mockUserReadService.findById(anyString())).thenReturn(expectedUserData);

        // Act
        Optional<UserData> result = userQueryService.findById("123");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedUserData.getId(), result.get().getId());
        assertEquals(expectedUserData.getEmail(), result.get().getEmail());
        assertEquals(expectedUserData.getUsername(), result.get().getUsername());
        assertEquals(expectedUserData.getBio(), result.get().getBio());
        assertEquals(expectedUserData.getImage(), result.get().getImage());
        verify(mockUserReadService, times(1)).findById("123");
    }

    @Test
    public void should_return_empty_when_not_found() {
        // Arrange
        when(mockUserReadService.findById(anyString())).thenReturn(null);

        // Act
        Optional<UserData> result = userQueryService.findById("123");

        // Assert
        assertFalse(result.isPresent());
        verify(mockUserReadService, times(1)).findById("123");
    }
}