package resources;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import ch.heigvd.amt.models.User;
import ch.heigvd.amt.resources.AccountResource;
import database.PostgisResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import java.security.SecureRandom;
import java.util.Random;
import javax.inject.Inject;
import javax.sql.DataSource;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/*
This test class contains integration tests rather than unit tests to verify the operation of the resource to create
a new account
 */
@QuarkusTest
@QuarkusTestResource(PostgisResource.class)
public class AccountResourceTest {

  @Inject DataSource dataSource;

  @BeforeEach
  void setupEach() {
    PostgisResource.runQuery(dataSource, "sql/reset_db.sql", "sql/insert_user.sql");
  }

  @Test
  void createNewAccounts() {

    for (int i = 0; i < 50; i++) {
      String username = "integration-test-member-" + i;

      // a random password is generated to simulate a real password that matches the security policy
      User userSigningUp = new User(username, generatePassword(), null);

      given()
          .when()
          .contentType("application/json")
          .body(userSigningUp)
          .post("/accounts/register")
          .then()
          .assertThat()
          .statusCode(201)
          .and()
          .contentType(ContentType.JSON)
          .and()
          .body("username", equalTo(username))
          .and()
          .body("role", equalTo("MEMBER"));
    }
  }

  @Test
  void createAlreadyExistingAccount() {
    User userSigningUp = new User("unit-test-member-1", "Pass1234!", null);

    given()
        .when()
        .contentType("application/json")
        .body(userSigningUp)
        .post("/accounts/register")
        .then()
        .assertThat()
        .statusCode(409)
        .and()
        .contentType(ContentType.JSON)
        .and()
        .body("error", equalTo(AccountResource.USER_ERROR));
  }

  @Test
  void createAccountWithIllegalPassword() {
    User userSigningUp = new User("integration-test-member-2", "pass", null);

    given()
        .when()
        .contentType("application/json")
        .body(userSigningUp)
        .post("/accounts/register")
        .then()
        .assertThat()
        .statusCode(400)
        .and()
        .contentType(ContentType.JSON)
        .and()
        .body("errors[0].property", equalTo("password"))
        .and()
        .body("errors[0].message", equalTo(AccountResource.PASSWORD_ERROR));
  }

  /**
   * Function to generate a random password in accord with the password policy
   *
   * @return generated password
   */
  private String generatePassword() {

    char[] allowedCharacters =
        ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789§]°-`´[¬{}_£€<>#$^+=!?*()@%&|¦ç/\\'\"¢~:;,.¨")
            .toCharArray();
    int minLength = 8, maxLength = 32; // length min and max of the password
    String password;

    do {
      password =
          RandomStringUtils.random(
              new Random().nextInt(maxLength - minLength + 1) + minLength,
              0,
              allowedCharacters.length - 1,
              false,
              false,
              allowedCharacters,
              new SecureRandom());
    } while (!password.matches(AccountResource.PASSWORD_POLICY_REGEX));

    return password;
  }
}
