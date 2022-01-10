package services;

import ch.heigvd.amt.database.UpdateResult;
import ch.heigvd.amt.database.UpdateStatus;
import ch.heigvd.amt.models.User;
import ch.heigvd.amt.services.UserService;
import database.PostgisResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import java.util.Optional;
import javax.inject.Inject;
import javax.sql.DataSource;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(PostgisResource.class)
public class UserServiceTest {

  @Inject DataSource dataSource;

  @Inject UserService userService;

  @BeforeEach
  void setupEach() {
    PostgisResource.runQuery(dataSource, "sql/reset_db.sql", "sql/insert_user.sql");
  }

  @Test
  void getNonExistingUser() {
    Assertions.assertTrue(userService.getUser("unit-test-member-3").isEmpty());
  }

  @Test
  void getExistingUser() {
    Optional<User> result = userService.getUser("unit-test-admin-1");
    Assertions.assertTrue(result.isPresent());
    User u = result.get();
    Assertions.assertEquals("unit-test-admin-1", u.getUsername());
  }

  @Test
  void addNewUser() {
    User u = new User("unit-test-member-4", "password", User.Role.MEMBER);
    userService.addUser(u);
    Assertions.assertTrue(userService.getUser(u.getUsername()).isPresent());
  }

  @Test
  void addAlreadyExistingUser() {
    UpdateResult updateResult =
        userService.addUser(new User("unit-test-member-2", "password", User.Role.MEMBER));
    Assertions.assertEquals(UpdateStatus.DUPLICATE, updateResult.getStatus());
  }

  @Test
  void addNewUserWithoutRole() {
    Assertions.assertThrows(
        UnableToExecuteStatementException.class,
        () -> userService.addUser(new User("unit-test-admin-2", "password", null)));
  }
}
