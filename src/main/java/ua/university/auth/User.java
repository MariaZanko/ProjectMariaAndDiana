package ua.university.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    private final String username;
    private String password;
    private Role role;
    private boolean blocked;

    @JsonCreator
    public User(@JsonProperty("username") String username,
                @JsonProperty("password") String password,
                @JsonProperty("role") Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.blocked = false;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
    public void setRole(Role role) { this.role = role; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return String.format("User[%s, role=%s, blocked=%s]", username, role, blocked);
    }
}
