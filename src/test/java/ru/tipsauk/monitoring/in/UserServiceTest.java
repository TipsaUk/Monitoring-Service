package ru.tipsauk.monitoring.in;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.model.UserActionType;
import ru.tipsauk.monitoring.model.UserRole;
import ru.tipsauk.monitoring.service.in.UserService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @Test
    void signUp_SuccessfulRegistration() {
        assertTrue(userService.signUp("testUser", "password"));
        assertTrue(userService.getAllUsers().size() > 1);
    }

    @Test
    void signUp_UserAlreadyRegistered() {
        assertFalse(userService.signUp("admin", "123"));
    }

    @Test
    void signUp_EmptyPassword() {
        assertFalse(userService.signUp("newUser", ""));
    }

    @Test
    void signIn_SuccessfulSignIn() {
        assertTrue(userService.signIn("admin", "123"));
        assertNotNull(userService.getSessionUser());
    }

    @Test
    void signIn_UserNotFound() {
        assertFalse(userService.signIn("empty", "123"));
    }

    @Test
    void signIn_IncorrectPassword() {
        assertFalse(userService.signIn("admin", "wrong_password"));
    }

    @Test
    void signOut_SuccessfulSignOut() {
        userService.signIn("admin", "123");
        userService.signOut();
        assertNull(userService.getSessionUser());
    }

    @Test
    void getUserByName_UserExists() {
        User user = userService.getUserByName("admin");
        assertNotNull(user);
        assertEquals("admin", user.getNickName());
    }

    @Test
    void getUserByName_UserNotFound() {
        User user = userService.getUserByName("empty");
        assertNull(user);
    }

    @Test
    void isUserAdministrator_AdminUser() {
        User adminUser = new User("admin", "123", UserRole.ADMINISTRATOR);
        assertTrue(userService.isUserAdministrator(adminUser));
    }

    @Test
    void isUserAdministrator_NonAdminUser() {
        User regularUser = new User("user", "password", UserRole.USER);
        assertFalse(userService.isUserAdministrator(regularUser));
    }

    @Test
    void getAllUsers() {
        assertTrue(userService.getAllUsers().size() > 0);
    }

    @Test
    void getUserActions_AllActions() {
        User user = mock(User.class);
        when(user.getUserActions()).thenReturn(mock(Map.class));
        assertNotNull(userService.getUserActions(user, null));
    }

    @Test
    void getUserActions_SpecificAction() {
        User user = mock(User.class);
        when(user.getUserActions()).thenReturn(mock(Map.class));
        assertNotNull(userService.getUserActions(user, UserActionType.SIGN_IN));
    }

}