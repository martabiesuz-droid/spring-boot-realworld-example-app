package io.spring.application.article;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.spring.application.ArticleQueryService;
import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager;
import io.spring.application.Page;
import io.spring.application.data.ArticleData;
import io.spring.application.data.ArticleDataList;
import io.spring.application.data.ArticleFavoriteCount;
import io.spring.application.data.ProfileData;
import io.spring.core.user.User;
import io.spring.infrastructure.mybatis.readservice.ArticleFavoritesReadService;
import io.spring.infrastructure.mybatis.readservice.ArticleReadService;
import io.spring.infrastructure.mybatis.readservice.UserRelationshipQueryService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArticleQueryServiceUnitTest {

  @Mock private ArticleReadService mockArticleReadService;

  @Mock private UserRelationshipQueryService mockUserRelationshipQueryService;

  @Mock private ArticleFavoritesReadService mockArticleFavoritesReadService;

  private ArticleQueryService articleQueryService;
  private User testUser;
  private ProfileData authorProfile;
  private ArticleData testArticleData;

  @BeforeEach
  void setUp() {
    articleQueryService =
        new ArticleQueryService(
            mockArticleReadService,
            mockUserRelationshipQueryService,
            mockArticleFavoritesReadService);

    testUser = new User("test@example.com", "testuser", "password", null, null);
    authorProfile = new ProfileData("author1", "author1", "author bio", "author.jpg", false);

    testArticleData =
        new ArticleData(
            "article-123", // id
            "test-slug", // slug
            "Test Title", // title
            "Test Description", // description
            "Test Body", // body
            false, // favorited
            0, // favoritesCount
            null, // createdAt
            null, // updatedAt
            Arrays.asList("java", "spring"), // tagList
            authorProfile, // profileData
            5 // readingTime
            );
  }

  @Test
  void should_return_empty_when_article_not_found_by_id() {
    when(mockArticleReadService.findById(anyString())).thenReturn(null);

    Optional<ArticleData> result = articleQueryService.findById("nonexistent-id", testUser);

    assertFalse(result.isPresent());
  }

  @Test
  void should_return_empty_when_article_not_found_by_slug() {
    when(mockArticleReadService.findBySlug(anyString())).thenReturn(null);

    Optional<ArticleData> result = articleQueryService.findBySlug("nonexistent-slug", testUser);

    assertFalse(result.isPresent());
  }

  @Test
  void should_not_fill_extra_info_when_user_is_null_by_id() {
    when(mockArticleReadService.findById("article-123")).thenReturn(testArticleData);

    Optional<ArticleData> result = articleQueryService.findById("article-123", null);

    assertTrue(result.isPresent());
    assertEquals(testArticleData, result.get());
    verify(mockArticleFavoritesReadService, never()).isUserFavorite(any(), any());
    verify(mockArticleFavoritesReadService, never()).articleFavoriteCount(any());
    verify(mockUserRelationshipQueryService, never()).isUserFollowing(any(), any());
  }

  @Test
  void should_fill_extra_info_when_user_present_by_id() {
    when(mockArticleReadService.findById("article-123")).thenReturn(testArticleData);
    when(mockArticleFavoritesReadService.isUserFavorite(testUser.getId(), "article-123"))
        .thenReturn(true);
    when(mockArticleFavoritesReadService.articleFavoriteCount("article-123")).thenReturn(5);
    when(mockUserRelationshipQueryService.isUserFollowing(testUser.getId(), "author1"))
        .thenReturn(true);

    Optional<ArticleData> result = articleQueryService.findById("article-123", testUser);

    assertTrue(result.isPresent());
    assertTrue(result.get().isFavorited());
    assertEquals(5, result.get().getFavoritesCount());
    assertTrue(result.get().getProfileData().isFollowing());
  }

  @Test
  void should_fill_extra_info_using_article_data_id_when_found_by_slug() {
    when(mockArticleReadService.findBySlug("test-slug")).thenReturn(testArticleData);
    when(mockArticleFavoritesReadService.isUserFavorite(testUser.getId(), "article-123"))
        .thenReturn(true);
    when(mockArticleFavoritesReadService.articleFavoriteCount("article-123")).thenReturn(5);
    when(mockUserRelationshipQueryService.isUserFollowing(testUser.getId(), "author1"))
        .thenReturn(true);

    Optional<ArticleData> result = articleQueryService.findBySlug("test-slug", testUser);

    assertTrue(result.isPresent());
    assertTrue(result.get().isFavorited());
    assertEquals(5, result.get().getFavoritesCount());
    assertTrue(result.get().getProfileData().isFollowing());

    // Verify that article ID (not slug) was used for extra info calls
    verify(mockArticleFavoritesReadService).isUserFavorite(testUser.getId(), "article-123");
    verify(mockArticleFavoritesReadService).articleFavoriteCount("article-123");
    verify(mockUserRelationshipQueryService).isUserFollowing(testUser.getId(), "author1");
  }

  @Test
  void should_return_empty_pager_when_no_articles_with_cursor() {
    CursorPageParameter<Instant> page =
        new CursorPageParameter<>(null, 20, CursorPager.Direction.NEXT);
    when(mockArticleReadService.findArticlesWithCursor(any(), any(), any(), any()))
        .thenReturn(new ArrayList<>());

    CursorPager<ArticleData> result =
        articleQueryService.findRecentArticlesWithCursor(
            "tag", "author", "favoritedBy", page, testUser);

    assertTrue(result.getData().isEmpty());
    assertFalse(result.hasNext());
    assertFalse(result.hasPrevious());
    verify(mockArticleReadService, never()).findArticles(any());
  }

  @Test
  void should_mark_hasNext_true_when_extra_article_exists() {
    CursorPageParameter<Instant> page =
        new CursorPageParameter<>(null, 2, CursorPager.Direction.NEXT);
    List<String> articleIds = new ArrayList<>(List.of("id1", "id2", "id3"));
    when(mockArticleReadService.findArticlesWithCursor(any(), any(), any(), any()))
        .thenReturn(articleIds);

    ProfileData profile1 = new ProfileData("author1", "author1", "", "", false);
    ProfileData profile2 = new ProfileData("author2", "author2", "", "", false);
    ArticleData article1 =
        new ArticleData(
            "id1",
            "slug1",
            "Title1",
            "Desc1",
            "Body1",
            false,
            0,
            null,
            null,
            Collections.emptyList(),
            profile1,
            5);
    ArticleData article2 =
        new ArticleData(
            "id2",
            "slug2",
            "Title2",
            "Desc2",
            "Body2",
            false,
            0,
            null,
            null,
            Collections.emptyList(),
            profile2,
            3);
    List<ArticleData> articles = new ArrayList<>(List.of(article1, article2));

    when(mockArticleReadService.findArticles(any())).thenReturn(articles);
    when(mockArticleFavoritesReadService.articlesFavoriteCount(any()))
        .thenReturn(
            List.of(new ArticleFavoriteCount("id1", 10), new ArticleFavoriteCount("id2", 5)));

    CursorPager<ArticleData> result =
        articleQueryService.findRecentArticlesWithCursor(
            "tag", "author", "favoritedBy", page, testUser);

    assertEquals(2, result.getData().size());
    assertTrue(result.hasNext());
  }

  @Test
  void should_return_empty_articleDataList_when_no_articleIds() {
    Page page = new Page(0, 20);
    when(mockArticleReadService.queryArticles(any(), any(), any(), any()))
        .thenReturn(new ArrayList<>());
    when(mockArticleReadService.countArticle(any(), any(), any())).thenReturn(0);

    ArticleDataList result =
        articleQueryService.findRecentArticles("tag", "author", "favoritedBy", page, testUser);

    assertTrue(result.getArticleDatas().isEmpty());
    assertEquals(0, result.getCount());
    verify(mockArticleReadService, never()).findArticles(any());
  }

  @Test
  void should_return_empty_feed_when_user_follows_nobody() {
    Page page = new Page(0, 20);
    when(mockUserRelationshipQueryService.followedUsers(anyString())).thenReturn(new ArrayList<>());

    ArticleDataList result = articleQueryService.findUserFeed(testUser, page);

    assertTrue(result.getArticleDatas().isEmpty());
    assertEquals(0, result.getCount());
  }

  @Test
  void should_return_empty_pager_when_user_follows_nobody_with_cursor() {
    CursorPageParameter<Instant> page =
        new CursorPageParameter<>(null, 20, CursorPager.Direction.NEXT);
    when(mockUserRelationshipQueryService.followedUsers(anyString())).thenReturn(new ArrayList<>());

    CursorPager<ArticleData> result = articleQueryService.findUserFeedWithCursor(testUser, page);

    assertTrue(result.getData().isEmpty());
    assertFalse(result.hasNext());
    assertFalse(result.hasPrevious());
  }

  @Test
  void should_handle_missing_favorite_count_gracefully() {
    Page page = new Page(0, 20);
    List<String> articleIds = new ArrayList<>(List.of("art1"));
    ProfileData profile = new ProfileData("author1", "author1", "", "", false);
    ArticleData article =
        new ArticleData(
            "art1",
            "slug1",
            "Title1",
            "Desc1",
            "Body1",
            false,
            0,
            null,
            null,
            Collections.emptyList(),
            profile,
            5);

    when(mockArticleReadService.queryArticles(any(), any(), any(), any())).thenReturn(articleIds);
    when(mockArticleReadService.countArticle(any(), any(), any())).thenReturn(1);
    when(mockArticleReadService.findArticles(any())).thenReturn(new ArrayList<>(List.of(article)));
    when(mockArticleFavoritesReadService.articlesFavoriteCount(any()))
        .thenReturn(Collections.emptyList());

    ArticleDataList result =
        articleQueryService.findRecentArticles("tag", "author", "favoritedBy", page, testUser);

    assertNotNull(result);
    assertEquals(1, result.getArticleDatas().size());
    assertEquals("art1", result.getArticleDatas().get(0).getId());
    assertEquals(0, result.getArticleDatas().get(0).getFavoritesCount());
  }

  @Test
  void should_reverse_articles_when_direction_prev_with_cursor() {
    CursorPageParameter<Instant> page =
        new CursorPageParameter<>(null, 2, CursorPager.Direction.PREV);
    List<String> articleIds = new ArrayList<>(List.of("id1", "id2"));
    when(mockArticleReadService.findArticlesWithCursor(any(), any(), any(), any()))
        .thenReturn(articleIds);

    ProfileData profile1 = new ProfileData("author1", "author1", "", "", false);
    ProfileData profile2 = new ProfileData("author2", "author2", "", "", false);
    ArticleData articleA =
        new ArticleData(
            "id1",
            "slug1",
            "Title1",
            "Desc1",
            "Body1",
            false,
            0,
            null,
            null,
            Collections.emptyList(),
            profile1,
            5);
    ArticleData articleB =
        new ArticleData(
            "id2",
            "slug2",
            "Title2",
            "Desc2",
            "Body2",
            false,
            0,
            null,
            null,
            Collections.emptyList(),
            profile2,
            3);
    List<ArticleData> articles = new ArrayList<>(List.of(articleA, articleB));

    when(mockArticleReadService.findArticles(any()))
        .thenAnswer(
            invocation -> {
              List<String> ids = invocation.getArgument(0);
              List<ArticleData> result = new ArrayList<>();
              for (String id : ids) {
                if (id.equals("id2")) {
                  result.add(articleB);
                } else if (id.equals("id1")) {
                  result.add(articleA);
                }
              }
              return result;
            });
    when(mockArticleFavoritesReadService.articlesFavoriteCount(any()))
        .thenReturn(Collections.emptyList());

    CursorPager<ArticleData> result =
        articleQueryService.findRecentArticlesWithCursor(null, null, null, page, testUser);

    assertEquals(2, result.getData().size());
    assertEquals("id2", result.getData().get(0).getId()); // articleB first (reversed)
    assertEquals("id1", result.getData().get(1).getId()); // articleA second (reversed)
    assertFalse(result.hasPrevious());
  }

  @Test
  void should_return_articles_and_fill_extra_info_when_user_follows_someone_with_cursor() {
    CursorPageParameter<Instant> page =
        new CursorPageParameter<>(null, 20, CursorPager.Direction.NEXT);
    when(mockUserRelationshipQueryService.followedUsers(anyString()))
        .thenReturn(new ArrayList<>(List.of("followedAuthor1")));

    ProfileData followedAuthorProfile =
        new ProfileData("followedAuthor1", "followedAuthor1", "", "", false);
    ArticleData articleComAutor =
        new ArticleData(
            "art1",
            "slug1",
            "Title1",
            "Desc1",
            "Body1",
            false,
            0,
            null,
            null,
            Collections.emptyList(),
            followedAuthorProfile,
            5);
    when(mockArticleReadService.findArticlesOfAuthorsWithCursor(any(), any()))
        .thenReturn(new ArrayList<>(List.of(articleComAutor)));

    when(mockArticleFavoritesReadService.articlesFavoriteCount(any()))
        .thenReturn(Collections.emptyList());
    when(mockUserRelationshipQueryService.followingAuthors(any(), any()))
        .thenReturn(Set.of("followedAuthor1"));

    CursorPager<ArticleData> result = articleQueryService.findUserFeedWithCursor(testUser, page);

    assertEquals(1, result.getData().size());
    assertEquals("art1", result.getData().get(0).getId());
    assertTrue(result.getData().get(0).getProfileData().isFollowing());
    assertFalse(result.hasNext());
  }

  @Test
  void should_mark_hasNext_false_when_user_feed_has_no_extra_with_cursor() {
    CursorPageParameter<Instant> page =
        new CursorPageParameter<>(null, 1, CursorPager.Direction.NEXT);
    when(mockUserRelationshipQueryService.followedUsers(anyString()))
        .thenReturn(new ArrayList<>(List.of("followedAuthor1")));

    ProfileData followedAuthorProfile =
        new ProfileData("followedAuthor1", "followedAuthor1", "", "", false);
    ArticleData articleComAutor =
        new ArticleData(
            "art1",
            "slug1",
            "Title1",
            "Desc1",
            "Body1",
            false,
            0,
            null,
            null,
            Collections.emptyList(),
            followedAuthorProfile,
            5);
    when(mockArticleReadService.findArticlesOfAuthorsWithCursor(any(), any()))
        .thenReturn(new ArrayList<>(List.of(articleComAutor)));

    when(mockArticleFavoritesReadService.articlesFavoriteCount(any()))
        .thenReturn(Collections.emptyList());
    when(mockUserRelationshipQueryService.followingAuthors(any(), any()))
        .thenReturn(Set.of("followedAuthor1"));

    CursorPager<ArticleData> result = articleQueryService.findUserFeedWithCursor(testUser, page);

    assertEquals(1, result.getData().size());
    assertEquals("art1", result.getData().get(0).getId());
    assertEquals("followedAuthor1", result.getData().get(0).getProfileData().getId());
    assertFalse(result.hasNext());
  }

  @Test
  void should_reverse_and_mark_hasPrevious_when_user_feed_with_cursor_prev_direction() {
    CursorPageParameter<Instant> page =
        new CursorPageParameter<>(null, 2, CursorPager.Direction.PREV);
    when(mockUserRelationshipQueryService.followedUsers(anyString()))
        .thenReturn(new ArrayList<>(List.of("followedAuthor1")));

    ProfileData profile1 = new ProfileData("followedAuthor1", "followedAuthor1", "", "", false);
    ArticleData articleA =
        new ArticleData(
            "id1",
            "slug1",
            "Title1",
            "Desc1",
            "Body1",
            false,
            0,
            null,
            null,
            Collections.emptyList(),
            profile1,
            5);
    ArticleData articleB =
        new ArticleData(
            "id2",
            "slug2",
            "Title2",
            "Desc2",
            "Body2",
            false,
            0,
            null,
            null,
            Collections.emptyList(),
            profile1,
            3);

    List<ArticleData> articles = new ArrayList<>(List.of(articleA, articleB));
    when(mockArticleReadService.findArticlesOfAuthorsWithCursor(any(), any())).thenReturn(articles);
    when(mockArticleFavoritesReadService.articlesFavoriteCount(any()))
        .thenReturn(Collections.emptyList());
    when(mockUserRelationshipQueryService.followingAuthors(any(), any())).thenReturn(Set.of());

    CursorPager<ArticleData> result = articleQueryService.findUserFeedWithCursor(testUser, page);

    assertEquals(2, result.getData().size());
    assertEquals("id2", result.getData().get(0).getId()); // articleB appears first (reversed)
    assertEquals("id1", result.getData().get(1).getId()); // articleA appears second (reversed)
    assertFalse(result.hasPrevious());
  }
}
