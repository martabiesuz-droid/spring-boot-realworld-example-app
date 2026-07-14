package io.spring.infrastructure.jpa.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.Timestamp;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InstantConverterTest {

  private InstantConverter converter;

  @BeforeEach
  public void setUp() {
    converter = new InstantConverter();
  }

  @Test
  public void should_convert_timestamp_to_instant_when_not_null() {
    Timestamp ts = Timestamp.from(Instant.now());
    Instant result = converter.convertToEntityAttribute(ts);
    
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(ts.toInstant());
  }

  @Test
  public void should_return_null_when_timestamp_is_null() {
    Instant result = converter.convertToEntityAttribute(null);
    
    assertNull(result);
  }
}