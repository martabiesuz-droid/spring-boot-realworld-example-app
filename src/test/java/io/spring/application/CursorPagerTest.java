package io.spring.application;

import static org.junit.jupiter.api.Assertions.*;

import io.spring.application.CursorPager.Direction;
import io.spring.application.data.CommentData;
import java.time.Instant;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class CursorPagerTest {

  @Test
  public void should_return_start_cursor_when_data_present() {
    // Arrange
    Instant now = Instant.now();
    CommentData commentData = new CommentData();
    commentData.setId("comment1");
    commentData.setBody("Test comment");
    commentData.setCreatedAt(now);
    commentData.setUpdatedAt(now);

    CursorPager<CommentData> pager =
        new CursorPager<>(Collections.singletonList(commentData), Direction.NEXT, false);

    // Act
    PageCursor startCursor = pager.getStartCursor();

    // Assert
    assertNotNull(startCursor);
    assertEquals(now.toEpochMilli(), Long.parseLong(startCursor.toString()));
    assertFalse(pager.hasNext()); // hasExtra = false, so hasNext() should be false
    assertFalse(pager.hasPrevious());
  }
}
