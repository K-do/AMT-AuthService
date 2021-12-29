package resources;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import ch.heigvd.amt.models.User;
import ch.heigvd.amt.resources.AuthResource;
import database.PostgisResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import javax.inject.Inject;
import javax.sql.DataSource;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(PostgisResource.class)
public class AuthResourceTest {

  @Inject DataSource dataSource;

  @Inject JWTParser parser;

  @BeforeEach
  void setupEach() {
    PostgisResource.runQuery(dataSource, "sql/reset_db.sql", "sql/insert_user.sql");
  }

  @Test
  void logInToExistingAccount() {
    User userLoggingIn = new User("unit-test-admin-1", "Pass1234!", null);

    given()
        .when()
        .contentType("application/json")
        .body(userLoggingIn)
        .post("/auth/login")
        .then()
        .assertThat()
        .statusCode(200)
        .and()
        .contentType(ContentType.JSON)
        .and()
        .body("token", notNullValue())
        .and()
        .body("account.username", equalTo("unit-test-admin-1"))
        .and()
        .body("account.role", equalTo("ADMIN"));
  }

  @Test
  void logInToExistingAccountWithWrongPassword() {
    User userLoggingIn = new User("unit-test-admin-1", "Pass1234?", null);

    given()
        .when()
        .contentType("application/json")
        .body(userLoggingIn)
        .post("/auth/login")
        .then()
        .assertThat()
        .statusCode(403)
        .and()
        .contentType(ContentType.JSON)
        .and()
        .body("error", equalTo(AuthResource.CREDENTIALS_ERROR));
  }

  @Test
  void logInToNonExistingAccount() {
    User userLoggingIn = new User("unit-test-admin-2", "Pass1234!", null);

    given()
        .when()
        .contentType("application/json")
        .body(userLoggingIn)
        .post("/auth/login")
        .then()
        .assertThat()
        .statusCode(403)
        .and()
        .contentType(ContentType.JSON)
        .and()
        .body("error", equalTo(AuthResource.CREDENTIALS_ERROR));
  }

  @Test
  void verifyJwtToken() throws ParseException {
    User userLoggingIn = new User("unit-test-admin-1", "Pass1234!", null);

    String token =
        given()
            .when()
            .contentType("application/json")
            .body(userLoggingIn)
            .post("/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .path("token");

    JsonWebToken jwt = parser.parse(token);
    Assertions.assertEquals("AMT-AuthService", jwt.getIssuer());
    Assertions.assertEquals(userLoggingIn.getUsername(), jwt.getSubject());
    Assertions.assertTrue(jwt.getGroups().contains("ADMIN"));
  }
}
