package io.spring.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.spring.application.CursorPager.Direction;
import org.junit.jupiter.api.Test;

class CursorPageParameterTest {

  @Test
  void should_return_limit_plus_one_as_query_limit() {
    CursorPageParameter<String> page = new CursorPageParameter<>(null, 20, Direction.NEXT);
    assertEquals(21, page.getQueryLimit());
  }

  @Test
  void should_use_exact_max_limit_when_limit_equals_max_boundary() {
    CursorPageParameter<String> page = new CursorPageParameter<>(null, 1000, Direction.NEXT);
    assertEquals(1000, page.getLimit());
  }

  @Test
  void should_cap_limit_when_exceeds_max() {
    CursorPageParameter<String> page = new CursorPageParameter<>(null, 1500, Direction.NEXT);
    assertEquals(1000, page.getLimit());
  }
}
