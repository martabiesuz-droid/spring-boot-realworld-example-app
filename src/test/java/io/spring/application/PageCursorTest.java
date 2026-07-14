package io.spring.application;

import java.time.Instant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PageCursorTest {

@Test
    public void should_return_data_as_string_in_toString() {
        // Arrange
        Instant testInstant = Instant.ofEpochMilli(1625097600000L); // 2021-06-30 12:00:00 UTC
        DateTimeCursor cursor = new DateTimeCursor(testInstant);

        // Act
        String result = cursor.toString();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("1625097600000", result);
        assertEquals(testInstant, cursor.getData());
    }

    @Test
    public void should_use_data_toString_when_not_overridden() {
        // Arrange
        TestCursor cursor = new TestCursor("hello");

        // Act
        String result = cursor.toString();

        // Assert
        assertEquals("hello", result);
    }

    static class TestCursor extends PageCursor<String> {
        TestCursor(String data) { 
            super(data); 
        }
    }
}