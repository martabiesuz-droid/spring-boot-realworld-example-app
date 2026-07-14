package io.spring.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("integration")
public class ContainerIntegrationTest {

  @BeforeAll
  static void setup() {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = 8080;
  }

  @Test
  void getTags_returnsTagsList() {
    given()
        .when()
        .get("/tags")
        .then()
        .statusCode(200)
        .body("tags", notNullValue())
        .body("tags", instanceOf(java.util.List.class));
  }

  @Test
  void getArticles_returnsArticlesList() {
    given()
        .when()
        .get("/articles")
        .then()
        .statusCode(200)
        .body("articles", notNullValue())
        .body("articlesCount", notNullValue());
  }

  @Test
  void registerUser_returnsUserWithToken() {
    long ts = System.currentTimeMillis();
    String body =
        String.format(
            "{\"user\":{\"email\":\"test%d@test.com\",\"username\":\"user%d\",\"password\":\"password123\"}}",
            ts, ts);
    given()
        .contentType(ContentType.JSON)
        .body(body)
        .when()
        .post("/users")
        .then()
        .statusCode(201)
        .body("user.token", notNullValue())
        .body("user.email", containsString("@test.com"));
  }

  @Test
  void loginWithInvalidCredentials_returns422() {
    String body = "{\"user\":{\"email\":\"nobody@nowhere.com\",\"password\":\"wrong\"}}";
    given()
        .contentType(ContentType.JSON)
        .body(body)
        .when()
        .post("/users/login")
        .then()
        .statusCode(anyOf(is(401), is(422), is(403)));
  }

  @Test
  void getUnknownProfile_returns401or404() {
    given().when().get("/profiles/nonexistentuser99999").then().statusCode(anyOf(is(401), is(404)));
  }

  @Test
  void swaggerUiIsAccessible() {
    given().when().get("/swagger-ui/index.html").then().statusCode(200);
  }
}
