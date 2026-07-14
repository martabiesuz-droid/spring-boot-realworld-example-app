package io.spring;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonCustomizations {

  @Bean
  public JavaTimeModule javaTimeModule() {
    return new JavaTimeModule();
  }
}
