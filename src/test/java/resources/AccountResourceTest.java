package resources;

import ch.heigvd.amt.models.User;
import database.PostgisResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.sql.DataSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@QuarkusTestResource(PostgisResource.class)
public class AccountResourceTest {

    private static final String PASSWORD_ERROR =
            "The password does not match the security politics, "
                    + "it should be at least 8 char long, should contain at least one uppercase char, one lowercase char, "
                    + "one digit and one special character";

    @Inject
    DataSource dataSource;

    @BeforeEach
    void setupEach() {
        PostgisResource.runQuery(
                dataSource, "sql/reset_db.sql", "sql/insert_user.sql");
    }

    @Test
    void createNewAccount() {
        User userSigningUp = new User("integration-test-member-1", "Pass1234!", null);

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
                    .body("username", equalTo("integration-test-member-1"))
                .and()
                    .body("role", equalTo("MEMBER"));
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
                    .body("error", equalTo("The username already exist"));
    }

    @Test
    void createAccountWithIllegalPassword() {
        User userSigningUp = new User("integration-test-member-2", "WeakPassword", null);

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
                    .body("errors[0].message", equalTo(PASSWORD_ERROR));
    }
}
