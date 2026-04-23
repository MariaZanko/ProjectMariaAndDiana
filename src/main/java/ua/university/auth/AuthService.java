package ua.university.auth;

import ua.university.exceptions.AccessDeniedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final List<User> users = new ArrayList<>();
    private User currentUser = null;

    public AuthService() {
        // Default users
        users.add(new User("admin", "123", Role.ADMIN));
        users.add(new User("manager", "123", Role.MANAGER));
        users.add(new User("user", "123", Role.USER));
    }

    public boolean login(String username, String password) {
        Optional<User> found = users.stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst();
        if (found.isPresent()) {
            if (found.get().isBlocked()) {
                log.warn("Blocked user tried to login: {}", username);
                System.out.println("Акаунт заблоковано.");
                return false;
            }
            currentUser = found.get();
            log.info("User logged in: {} with role {}", username, currentUser.getRole());
            return true;
        }
        log.warn("Failed login attempt for: {}", username);
        return false;
    }

    public void logout() {
        log.info("User logged out: {}", currentUser != null ? currentUser.getUsername() : "none");
        currentUser = null;
    }

    public User getCurrentUser() { return currentUser; }

    public boolean isLoggedIn() { return currentUser != null; }

    public void requireRole(Role required) {
        if (currentUser == null) throw new AccessDeniedException("Потрібна авторизація");
        if (currentUser.getRole().ordinal() < required.ordinal()) {
            throw new AccessDeniedException("Недостатньо прав. Потрібна роль: " + required);
        }
    }

    public List<User> getAllUsers() { return new ArrayList<>(users); }

    public void addUser(String username, String password, Role role) {
        requireRole(Role.ADMIN);
        users.add(new User(username, password, role));
        log.info("New user created: {}", username);
    }

    public void blockUser(String username) {
        requireRole(Role.ADMIN);
        users.stream().filter(u -> u.getUsername().equals(username)).findFirst()
                .ifPresent(u -> { u.setBlocked(true); log.info("User blocked: {}", username); });
    }

    public void unblockUser(String username) {
        requireRole(Role.ADMIN);
        users.stream().filter(u -> u.getUsername().equals(username)).findFirst()
                .ifPresent(u -> { u.setBlocked(false); log.info("User unblocked: {}", username); });
    }
}
