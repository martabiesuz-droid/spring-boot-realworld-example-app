package io.spring.application.comment;

import io.spring.application.CommentQueryService;
import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.user.User;
import io.spring.infrastructure.mybatis.readservice.CommentReadService;
import io.spring.infrastructure.mybatis.readservice.UserRelationshipQueryService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentQueryServiceUnitTest {

    @Mock
    private CommentReadService mockCommentReadService;
    
    @Mock
    private UserRelationshipQueryService mockUserRelationshipQueryService;
    
    private CommentQueryService commentQueryService;
    private User testUser;
    private ProfileData author1Profile;
    private ProfileData author2Profile;
    private CommentData comment1;
    private CommentData comment2;
    private CommentData comment3;
    
    @BeforeEach
    void setUp() {
        commentQueryService = new CommentQueryService(mockCommentReadService, mockUserRelationshipQueryService);
        
        testUser = new User("test@example.com", "testuser", "password", null, null);
        
        author1Profile = new ProfileData("author1", "username1", "", "", false);
        author2Profile = new ProfileData("author2", "username2", "", "", false);
        
        comment1 = new CommentData("comment1", "Body 1", "article1", null, null, author1Profile);
        comment2 = new CommentData("comment2", "Body 2", "article1", null, null, author2Profile);
        comment3 = new CommentData("comment3", "Body 3", "article1", null, null, author1Profile);
    }
    
    @Test
    void should_return_empty_when_comment_not_found() {
        when(mockCommentReadService.findById(anyString())).thenReturn(null);
        
        Optional<CommentData> result = commentQueryService.findById("nonexistent", testUser);
        
        assertFalse(result.isPresent());
    }
    
    @Test
    void should_set_following_true_when_user_follows_author() {
        when(mockCommentReadService.findById("comment1")).thenReturn(comment1);
        when(mockUserRelationshipQueryService.isUserFollowing(anyString(), eq("author1"))).thenReturn(true);
        
        Optional<CommentData> result = commentQueryService.findById("comment1", testUser);
        
        assertTrue(result.isPresent());
        assertTrue(result.get().getProfileData().isFollowing());
    }
    
    @Test
    void should_set_following_false_when_user_does_not_follow_author() {
        when(mockCommentReadService.findById("comment1")).thenReturn(comment1);
        when(mockUserRelationshipQueryService.isUserFollowing(anyString(), eq("author1"))).thenReturn(false);
        
        Optional<CommentData> result = commentQueryService.findById("comment1", testUser);
        
        assertTrue(result.isPresent());
        assertFalse(result.get().getProfileData().isFollowing());
    }
    
@Test
    void should_return_empty_list_when_no_comments() {
        when(mockCommentReadService.findByArticleId(anyString())).thenReturn(new ArrayList<>());
        
        List<CommentData> result = commentQueryService.findByArticleId("article1", testUser);
        
        assertTrue(result.isEmpty());
        verify(mockUserRelationshipQueryService, never()).followingAuthors(any(), any());
    }
    
@Test
    void should_skip_following_logic_when_user_is_null() {
        List<CommentData> comments = new ArrayList<>(List.of(comment1));
        when(mockCommentReadService.findByArticleId(anyString())).thenReturn(comments);
        
        List<CommentData> result = commentQueryService.findByArticleId("article1", null);
        
        assertEquals(1, result.size());
        assertFalse(result.get(0).getProfileData().isFollowing());
    }
    
@Test
    void should_set_following_true_only_for_followed_authors() {
        List<CommentData> comments = new ArrayList<>(List.of(comment1, comment2));
        when(mockCommentReadService.findByArticleId(anyString())).thenReturn(comments);
        when(mockUserRelationshipQueryService.followingAuthors(eq(testUser.getId()), eq(List.of("author1", "author2")))).thenReturn(Set.of("author1"));

        List<CommentData> result = commentQueryService.findByArticleId("article1", testUser);

        assertEquals(2, result.size());
        assertTrue(result.get(0).getProfileData().isFollowing());
        assertFalse(result.get(1).getProfileData().isFollowing());
    }

    @Test
    void should_not_call_followingAuthors_when_comments_list_is_empty_boundary() {
        List<CommentData> comments = new ArrayList<>(List.of(comment1));
        when(mockCommentReadService.findByArticleId(anyString())).thenReturn(comments);
        when(mockUserRelationshipQueryService.followingAuthors(anyString(), any())).thenReturn(Set.of("author1"));

        List<CommentData> result = commentQueryService.findByArticleId("article1", testUser);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getProfileData().isFollowing());
    }
    
    @Test
    void should_return_empty_pager_when_no_comments() {
        CursorPageParameter<Instant> page = new CursorPageParameter<>(null, 20, CursorPager.Direction.NEXT);
        when(mockCommentReadService.findByArticleIdWithCursor(anyString(), any())).thenReturn(new ArrayList<>());
        
        CursorPager<CommentData> result = commentQueryService.findByArticleIdWithCursor("article1", testUser, page);
        
        assertTrue(result.getData().isEmpty());
        assertFalse(result.hasNext());
        assertFalse(result.hasPrevious());
    }
    
@Test
    void should_set_following_when_user_present_with_cursor() {
        CursorPageParameter<Instant> page = new CursorPageParameter<>(null, 20, CursorPager.Direction.NEXT);
        List<CommentData> comments = new ArrayList<>(List.of(comment1));
        when(mockCommentReadService.findByArticleIdWithCursor(anyString(), any())).thenReturn(comments);
        when(mockUserRelationshipQueryService.followingAuthors(eq(testUser.getId()), eq(List.of("author1")))).thenReturn(Set.of("author1"));
        
        CursorPager<CommentData> result = commentQueryService.findByArticleIdWithCursor("article1", testUser, page);
        
        assertEquals(1, result.getData().size());
        assertTrue(result.getData().get(0).getProfileData().isFollowing());
    }
    
    @Test
    void should_skip_following_when_user_is_null_with_cursor() {
        CursorPageParameter<Instant> page = new CursorPageParameter<>(null, 20, CursorPager.Direction.NEXT);
        List<CommentData> comments = new ArrayList<>(List.of(comment1));
        when(mockCommentReadService.findByArticleIdWithCursor(anyString(), any())).thenReturn(comments);
        
        CursorPager<CommentData> result = commentQueryService.findByArticleIdWithCursor("article1", null, page);
        
        assertEquals(1, result.getData().size());
        assertFalse(result.getData().get(0).getProfileData().isFollowing());
    }
    
    @Test
    void should_mark_hasNext_true_when_extra_item_exists() {
        CursorPageParameter<Instant> page = new CursorPageParameter<>(null, 2, CursorPager.Direction.NEXT);
        List<CommentData> comments = new ArrayList<>(List.of(comment1, comment2, comment3));
        when(mockCommentReadService.findByArticleIdWithCursor(anyString(), any())).thenReturn(comments);
        
        CursorPager<CommentData> result = commentQueryService.findByArticleIdWithCursor("article1", testUser, page);
        
        assertEquals(2, result.getData().size());
        assertTrue(result.hasNext());
    }
    
    @Test
    void should_mark_hasNext_false_when_no_extra_item() {
        CursorPageParameter<Instant> page = new CursorPageParameter<>(null, 2, CursorPager.Direction.NEXT);
        List<CommentData> comments = new ArrayList<>(List.of(comment1, comment2));
        when(mockCommentReadService.findByArticleIdWithCursor(anyString(), any())).thenReturn(comments);
        
        CursorPager<CommentData> result = commentQueryService.findByArticleIdWithCursor("article1", testUser, page);
        
        assertEquals(2, result.getData().size());
        assertFalse(result.hasNext());
    }
    
    @Test
    void should_reverse_order_when_direction_is_prev() {
        CursorPageParameter<Instant> page = new CursorPageParameter<>(null, 2, CursorPager.Direction.PREV);
        List<CommentData> comments = new ArrayList<>(List.of(comment1, comment2));
        when(mockCommentReadService.findByArticleIdWithCursor(anyString(), any())).thenReturn(comments);
        
        CursorPager<CommentData> result = commentQueryService.findByArticleIdWithCursor("article1", testUser, page);
        
        assertEquals(2, result.getData().size());
        assertEquals("comment2", result.getData().get(0).getId());
        assertEquals("comment1", result.getData().get(1).getId());
        assertFalse(result.hasPrevious());
    }
    
    @Test
    void should_reverse_order_when_direction_is_prev_with_extra_item() {
        CursorPageParameter<Instant> page = new CursorPageParameter<>(null, 1, CursorPager.Direction.PREV);
        List<CommentData> comments = new ArrayList<>(List.of(comment1, comment2));
        when(mockCommentReadService.findByArticleIdWithCursor(anyString(), any())).thenReturn(comments);
        
        CursorPager<CommentData> result = commentQueryService.findByArticleIdWithCursor("article1", testUser, page);
        
assertEquals(1, result.getData().size());
        assertEquals("comment1", result.getData().get(0).getId());
        assertTrue(result.hasPrevious());
    }
    
@Test
    void should_set_following_true_only_for_specific_followed_author_with_cursor() {
        CursorPageParameter<Instant> page = new CursorPageParameter<>(null, 20, CursorPager.Direction.NEXT);
        
        ProfileData profile1 = new ProfileData("author1", "author1", "bio1", "image1", false);
        ProfileData profile2 = new ProfileData("author2", "author2", "bio2", "image2", false);
        
        CommentData comment1 = new CommentData("comment1", "body1", "article1", Instant.now(), 
            null, new ProfileData("author1", "author1", "bio1", "image1", false));
        CommentData comment2 = new CommentData("comment2", "body2", "article1", Instant.now(), 
            null, new ProfileData("author2", "author2", "bio2", "image2", false));
        
        List<CommentData> comments = new ArrayList<>(List.of(comment1, comment2));
        when(mockCommentReadService.findByArticleIdWithCursor(anyString(), any())).thenReturn(comments);
        when(mockUserRelationshipQueryService.followingAuthors(anyString(), any())).thenReturn(Set.of("author1"));
        
        CursorPager<CommentData> result = commentQueryService.findByArticleIdWithCursor("article1", testUser, page);
        
        assertEquals(2, result.getData().size());
        assertTrue(result.getData().get(0).getId().equals("comment1") || result.getData().get(0).getId().equals("comment2"));
        assertTrue(result.getData().get(1).getId().equals("comment1") || result.getData().get(1).getId().equals("comment2"));
        
        for (CommentData comment : result.getData()) {
            if (comment.getProfileData().getId().equals("author1")) {
                assertTrue(comment.getProfileData().isFollowing());
            } else if (comment.getProfileData().getId().equals("author2")) {
                assertFalse(comment.getProfileData().isFollowing());
            }
        }
    }

    @Test
    void should_set_following_true_only_for_specific_followed_author_without_cursor() {
        List<CommentData> comments = new ArrayList<>(List.of(comment1, comment2));
        when(mockCommentReadService.findByArticleId(anyString())).thenReturn(comments);
        when(mockUserRelationshipQueryService.followingAuthors(anyString(), any())).thenReturn(Set.of("author1"));
        
        List<CommentData> result = commentQueryService.findByArticleId("article1", testUser);
        
        assertEquals(2, result.size());
        assertTrue(result.get(0).getId().equals("comment1") || result.get(0).getId().equals("comment2"));
        assertTrue(result.get(1).getId().equals("comment1") || result.get(1).getId().equals("comment2"));
        
        for (CommentData comment : result) {
            if (comment.getProfileData().getId().equals("author1")) {
                assertTrue(comment.getProfileData().isFollowing());
            } else if (comment.getProfileData().getId().equals("author2")) {
                assertFalse(comment.getProfileData().isFollowing());
            }
        }
    }
}