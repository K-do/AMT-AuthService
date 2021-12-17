package ch.heigvd.amt.services;

import ch.heigvd.amt.database.UpdateResult;
import ch.heigvd.amt.database.UpdateResultHandler;
import ch.heigvd.amt.models.User;
import ch.heigvd.amt.utils.ResourceLoader;
import io.quarkus.elytron.security.common.BcryptUtil;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;

/** */
@ApplicationScoped
public class UserService {

  private final Jdbi jdbi;
  private final UpdateResultHandler updateResultHandler;

  @Inject
  public UserService(Jdbi jdbi, UpdateResultHandler updateResultHandler) {
    this.jdbi = jdbi;
    this.updateResultHandler = updateResultHandler;
  }

  /**
   * Add user to the database
   *
   * @param user the new user to create
   * @return the result of the operation
   */
  public UpdateResult addUser(User user) {
    try {
      jdbi.useHandle(
          handle ->
              handle
                  .createUpdate(ResourceLoader.loadResource("sql/user/add.sql"))
                  .bind("username", user.getUsername())
                  .bind("password", BcryptUtil.bcryptHash(user.getPassword()))
                  .bind("role", user.getRole())
                  .execute());
    } catch (UnableToExecuteStatementException e) {
      return updateResultHandler.handleUpdateError(e);
    }
    return UpdateResult.success();
  }

  /**
   * Get a user from the database
   *
   * @param username the username of the user
   * @return an optional. Contains the user if it exists
   */
  public Optional<User> getUser(String username) {
    return jdbi.withHandle(
        handle ->
            handle
                .createQuery(ResourceLoader.loadResource("sql/user/get.sql"))
                .bind("username", username)
                .mapTo(User.class)
                .findOne());
  }
}
