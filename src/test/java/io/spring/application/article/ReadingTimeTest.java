package io.spring.application.article;

import io.spring.application.ReadingTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReadingTimeTest {

  @Test
  public void empty_body_returns_minimum_one() {
    Assertions.assertEquals(1, ReadingTime.minutes(""));
  }

  @Test
  public void null_body_returns_minimum_one() {
    Assertions.assertEquals(1, ReadingTime.minutes(null));
  }

  @Test
  public void short_body_returns_minimum_one() {
    Assertions.assertEquals(1, ReadingTime.minutes("just a few words"));
  }

  @Test
  public void exactly_two_hundred_words_is_one_minute() {
    String body = ("word ".repeat(200)).trim();
    Assertions.assertEquals(1, ReadingTime.minutes(body));
  }

  @Test
  public void two_hundred_one_words_is_two_minutes() {
    String body = ("word ".repeat(201)).trim();
    Assertions.assertEquals(2, ReadingTime.minutes(body));
  }

  @Test
  public void long_body_rounds_up() {
    String body = ("word ".repeat(450)).trim();
    Assertions.assertEquals(3, ReadingTime.minutes(body));
  }
}
