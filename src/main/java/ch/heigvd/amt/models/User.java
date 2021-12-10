package ch.heigvd.amt.models;

import org.jdbi.v3.core.mapper.Nested;

import java.beans.ConstructorProperties;

public class User {

    enum Role {
        MEMBER, ADMIN
    }

    private final String username;
    private final String password;
    private final Role role;

    @ConstructorProperties({"username", "password", "role"})
    public User(String username, String password, @Nested("role") Role role) {
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

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }

}
