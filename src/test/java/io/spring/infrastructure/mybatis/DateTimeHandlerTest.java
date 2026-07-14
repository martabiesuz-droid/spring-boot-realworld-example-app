package io.spring.infrastructure.mybatis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DateTimeHandlerTest {

  @Mock
  private PreparedStatement mockPs;

  @Mock
  private ResultSet mockRs;

  private DateTimeHandler handler;

  @BeforeEach
  public void setUp() {
    handler = new DateTimeHandler();
  }

  @Test
  public void should_set_timestamp_when_parameter_is_not_null() throws SQLException {
    Instant parameter = Instant.now();
    handler.setParameter(mockPs, 1, parameter, null);
    verify(mockPs).setTimestamp(eq(1), any(Timestamp.class), any(java.util.Calendar.class));
  }

  @Test
  public void should_set_null_timestamp_when_parameter_is_null() throws SQLException {
    handler.setParameter(mockPs, 1, null, null);
    verify(mockPs).setTimestamp(eq(1), isNull(), any(java.util.Calendar.class));
  }

  @Test
  public void should_return_instant_when_timestamp_is_not_null_by_column_name() throws SQLException {
    Instant expectedInstant = Instant.now();
    when(mockRs.getTimestamp(eq("created_at"), any(java.util.Calendar.class)))
        .thenReturn(Timestamp.from(expectedInstant));

    Instant result = handler.getResult(mockRs, "created_at");

    assertThat(result).isNotNull();
  }

  @Test
  public void should_return_null_when_timestamp_is_null_by_column_name() throws SQLException {
    when(mockRs.getTimestamp(eq("created_at"), any(java.util.Calendar.class))).thenReturn(null);

    Instant result = handler.getResult(mockRs, "created_at");

    assertThat(result).isNull();
  }

  @Test
  public void should_return_instant_when_timestamp_is_not_null_by_column_index() throws SQLException {
    Instant expectedInstant = Instant.now();
    when(mockRs.getTimestamp(eq(1), any(java.util.Calendar.class)))
        .thenReturn(Timestamp.from(expectedInstant));

    Instant result = handler.getResult(mockRs, 1);

    assertThat(result).isNotNull();
  }

  @Test
  public void should_return_null_when_timestamp_is_null_by_column_index() throws SQLException {
    when(mockRs.getTimestamp(eq(1), any(java.util.Calendar.class))).thenReturn(null);

    Instant result = handler.getResult(mockRs, 1);

    assertThat(result).isNull();
  }
}