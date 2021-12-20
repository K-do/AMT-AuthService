package ch.heigvd.amt.models;

import java.beans.ConstructorProperties;

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

  @ConstructorProperties({"username", "password", "role"})
  public User(String username, String password, Role role) {
    this.username = username;
    this.password = password;
    this.role = role;
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
