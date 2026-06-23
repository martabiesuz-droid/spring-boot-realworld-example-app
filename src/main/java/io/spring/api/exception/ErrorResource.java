package io.spring.api.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorResource {
  private Map<String, List<String>> errors;

  public ErrorResource(List<FieldErrorResource> fieldErrors) {
    this.errors = new HashMap<>();
    for (FieldErrorResource fieldError : fieldErrors) {
      errors.computeIfAbsent(fieldError.getField(), key -> new ArrayList<>())
          .add(fieldError.getMessage());
    }
  }

  public Map<String, List<String>> getErrors() {
    return errors;
  }
}
