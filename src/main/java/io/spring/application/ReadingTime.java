package io.spring.application;

public final class ReadingTime {
  private static final int WORDS_PER_MINUTE = 200;

  private ReadingTime() {}

  public static int minutes(String body) {
    if (body == null || body.trim().isEmpty()) {
      return 1;
    }
    int words = body.trim().split("\\s+").length;
    int minutes = (int) Math.ceil((double) words / WORDS_PER_MINUTE);
    return Math.max(1, minutes);
  }
}
