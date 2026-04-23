package ua.university;

import org.junit.jupiter.api.*;
import ua.university.auth.*;
import ua.university.exceptions.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AuthService Tests")
class AuthServiceTest {

    private AuthService auth;

    @BeforeEach
    void setup() { auth = new AuthService(); }

    @Test
    @DisplayName("Успішний логін")
    void testLoginSuccess() {
        assertTrue(auth.login("admin", "admin123"));
        assertTrue(auth.isLoggedIn());
        assertEquals(Role.ADMIN, auth.getCurrentUser().getRole());
    }

    @Test
    @DisplayName("Невірний пароль")
    void testLoginFail() {
        assertFalse(auth.login("admin", "wrongpassword"));
        assertFalse(auth.isLoggedIn());
    }

    @Test
    @DisplayName("Вихід із системи")
    void testLogout() {
        auth.login("user", "user123");
        auth.logout();
        assertFalse(auth.isLoggedIn());
    }

    @Test
    @DisplayName("USER не може вимагати роль MANAGER")
    void testRoleCheck() {
        auth.login("user", "user123");
        assertThrows(AccessDeniedException.class, () -> auth.requireRole(Role.MANAGER));
    }

    @Test
    @DisplayName("ADMIN може все")
    void testAdminRequireAll() {
        auth.login("admin", "admin123");
        assertDoesNotThrow(() -> auth.requireRole(Role.MANAGER));
        assertDoesNotThrow(() -> auth.requireRole(Role.ADMIN));
    }

    @Test
    @DisplayName("Блокування користувача")
    void testBlockUser() {
        auth.login("admin", "admin123");
        auth.blockUser("user");
        auth.logout();
        assertFalse(auth.login("user", "user123"));
    }

    @Test
    @DisplayName("Додавання нового користувача")
    void testAddUser() {
        auth.login("admin", "admin123");
        auth.addUser("newuser", "pass123", Role.USER);
        auth.logout();
        assertTrue(auth.login("newuser", "pass123"));
    }
}
