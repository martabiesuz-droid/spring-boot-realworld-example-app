package io.spring.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PageTest {

  @Test
  void should_use_default_offset_when_offset_is_zero() {
    Page page = new Page(0, 20);
    assertEquals(0, page.getOffset());
  }

  @Test
  void should_use_default_offset_when_offset_is_negative() {
    Page page = new Page(-5, 20);
    assertEquals(0, page.getOffset());
  }

  @Test
  void should_use_given_offset_when_positive() {
    Page page = new Page(10, 20);
    assertEquals(10, page.getOffset());
  }

  @Test
  void should_use_default_limit_when_limit_is_zero() {
    Page page = new Page(0, 0);
    assertEquals(20, page.getLimit());
  }

  @Test
  void should_use_default_limit_when_limit_is_negative() {
    Page page = new Page(0, -5);
    assertEquals(20, page.getLimit());
  }

  @Test
  void should_use_given_limit_when_between_zero_and_max() {
    Page page = new Page(0, 50);
    assertEquals(50, page.getLimit());
  }

  @Test
  void should_cap_limit_at_max_when_limit_exceeds_max() {
    Page page = new Page(0, 150);
    assertEquals(100, page.getLimit());
  }

  @Test
  void should_use_exact_max_limit_when_limit_equals_max() {
    Page page = new Page(0, 100);
    assertEquals(100, page.getLimit());
  }

  @Test
  void should_use_given_offset_when_offset_equals_one_boundary() {
    Page page = new Page(1, 20);
    assertEquals(1, page.getOffset());
  }
}
