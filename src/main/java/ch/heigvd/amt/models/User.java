package ch.heigvd.amt.models;

import java.beans.ConstructorProperties;
import java.util.Objects;

/** Class representing a user composed of a username, a password and a role */
public class User {

  /** Enum with possible user roles */
  public enum Role {
    MEMBER,
    ADMIN
  }

  private final String username;
  private final String password;
  private Role role;

  /**
   * Constructor allowing to create a new user
   * @param username username of the user
   * @param password plaintext password of the user
   * @param role role of the user
   */
  @ConstructorProperties({"username", "password", "role"})
  public User(String username, String password, Role role) {

    Objects.requireNonNull(username);
    Objects.requireNonNull(password);

    this.username = username;
    this.password = password;
    this.role = role; // Is null when a user creates a new account from the application service, but it has to be set !
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    Objects.requireNonNull(role);
    this.role = role;
  }

  @Override
  public String toString() {
    return "User{"
        + "username='"
        + username
        + '\''
        + ", password='"
        + password
        + '\''
        + ", role="
        + role
        + '}';
  }
}
