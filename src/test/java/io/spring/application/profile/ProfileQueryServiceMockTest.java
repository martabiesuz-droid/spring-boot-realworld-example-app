package io.spring.application.profile;

import io.spring.application.ProfileQueryService;
import io.spring.application.data.ProfileData;
import io.spring.application.data.UserData;
import io.spring.core.user.User;
import io.spring.infrastructure.mybatis.readservice.UserReadService;
import io.spring.infrastructure.mybatis.readservice.UserRelationshipQueryService;
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
public class ProfileQueryServiceMockTest {

    @Mock
    private UserReadService mockUserReadService;
    
    @Mock
    private UserRelationshipQueryService mockUserRelationshipQueryService;
    
    @InjectMocks
    private ProfileQueryService profileQueryService;

    @Test
    public void should_not_check_following_when_current_user_is_null() {
        // Arrange
        UserData userData = new UserData("123", "test@example.com", "testuser", "test bio", "test.jpg");
        when(mockUserReadService.findByUsername(anyString())).thenReturn(userData);
        
        // Act
        Optional<ProfileData> result = profileQueryService.findByUsername("testuser", null);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals("123", result.get().getId());
        assertEquals("testuser", result.get().getUsername());
        assertEquals("test bio", result.get().getBio());
        assertEquals("test.jpg", result.get().getImage());
        assertFalse(result.get().isFollowing());
        verify(mockUserRelationshipQueryService, never()).isUserFollowing(any(), any());
        verify(mockUserReadService, times(1)).findByUsername("testuser");
    }

    @Test
    public void should_check_following_when_current_user_present() {
        // Arrange
        UserData userData = new UserData("123", "test@example.com", "testuser", "test bio", "test.jpg");
        User currentUser = new User("current@test.com", "current", "123", "", "");
        
        when(mockUserReadService.findByUsername(anyString())).thenReturn(userData);
        when(mockUserRelationshipQueryService.isUserFollowing(anyString(), anyString())).thenReturn(true);
        
        // Act
        Optional<ProfileData> result = profileQueryService.findByUsername("testuser", currentUser);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals("123", result.get().getId());
        assertEquals("testuser", result.get().getUsername());
        assertEquals("test bio", result.get().getBio());
        assertEquals("test.jpg", result.get().getImage());
        assertTrue(result.get().isFollowing());
        verify(mockUserRelationshipQueryService, times(1)).isUserFollowing(currentUser.getId(), userData.getId());
        verify(mockUserReadService, times(1)).findByUsername("testuser");
    }
}