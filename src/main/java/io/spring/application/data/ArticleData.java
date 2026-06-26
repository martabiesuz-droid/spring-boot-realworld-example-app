package io.spring.application.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.spring.application.DateTimeCursor;
import io.spring.application.ReadingTime;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleData implements io.spring.application.Node {
  private String id;
  private String slug;
  private String title;
  private String description;
  private String body;
  private boolean favorited;
  private int favoritesCount;
  private Instant createdAt;
  private Instant updatedAt;
  private List<String> tagList;

  @JsonProperty("author")
  private ProfileData profileData;

  @JsonProperty("readingTimeMinutes")
  public int getReadingTimeMinutes() {
    return ReadingTime.minutes(body);
  }

  @Override
  public DateTimeCursor getCursor() {
    return new DateTimeCursor(updatedAt);
  }
}
