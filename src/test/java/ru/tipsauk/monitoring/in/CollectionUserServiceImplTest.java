package ru.tipsauk.monitoring.in;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.model.UserActionType;
import ru.tipsauk.monitoring.model.UserRole;
import ru.tipsauk.monitoring.service.in.CollectionServicImpl.CollectionUserServiceImpl;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CollectionUserServiceImplTest {

    private CollectionUserServiceImpl collectionUserService;

    @BeforeEach
    void setUp() {
        collectionUserService = new CollectionUserServiceImpl();
    }

    @Test
    @DisplayName("Регистрация пользователя")
    void signUp_SuccessfulRegistration() {
        assertThat(collectionUserService.signUp("testUser", "password")).isTrue();
        assertThat(collectionUserService.getAllUsers().size() > 1).isTrue();
    }

    @Test
    @DisplayName("Дублирование пользователя")
    void signUp_UserAlreadyRegistered() {
        assertThat(collectionUserService.signUp("admin", "123")).isFalse();
    }

    @Test
    @DisplayName("Пустой пароль")
    void signUp_EmptyPassword() {
        assertThat(collectionUserService.signUp("newUser", "")).isFalse();
    }

    @Test
    @DisplayName("Вход пользователя в систему")
    void signIn_SuccessfulSignIn() {
        assertThat(collectionUserService.signIn("admin", "123")).isTrue();
        assertThat(collectionUserService.getSessionUser()).isNotNull();
    }

    @Test
    @DisplayName("Вход с неправильным логином")
    void signIn_UserNotFound() {
        assertThat(collectionUserService.signIn("empty", "123")).isFalse();
    }

    @Test
    @DisplayName("Вход с неправильным паролем")
    void signIn_IncorrectPassword() {
        assertThat(collectionUserService.signIn("admin", "wrong_password")).isFalse();
    }

    @Test
    @DisplayName("Выход пользователя из системы")
    void signOut_SuccessfulSignOut() {
        collectionUserService.signIn("admin", "123");
        collectionUserService.signOut();
        assertThat(collectionUserService.getSessionUser()).isNull();
    }

    @Test
    @DisplayName("Поиск пользователя по логину")
    void getUserByName_UserExists() {
        User user = collectionUserService.getUserByName("admin");
        assertThat(user).isNotNull();
        assertThat(user.getNickName()).isEqualTo("admin");
    }

    @Test
    @DisplayName("Поиск пользователя по неверному логину")
    void getUserByName_UserNotFound() {
        User user = collectionUserService.getUserByName("empty");
        assertThat(user).isNull();
    }

    @Test
    @DisplayName("Проверка есть права администратора")
    void isUserAdministrator_AdminUser() {
        User adminUser = new User("admin", "123", UserRole.ADMINISTRATOR);
        assertThat(adminUser.isUserAdministrator()).isTrue();
    }

    @Test
    @DisplayName("Проверка нет прав администратора")
    void isUserAdministrator_NonAdminUser() {
        User regularUser = new User("user", "password", UserRole.USER);
        assertThat(regularUser.isUserAdministrator()).isFalse();
    }

    @Test
    @DisplayName("Получение всех пользователей")
    void getAllUsers() {
        assertThat(collectionUserService.getAllUsers().size() > 0).isTrue();
    }

    @Test
    @DisplayName("Получение всех действий пользователя")
    void getUserActions_AllActions() {
        User user = mock(User.class);
        when(user.getUserActions()).thenReturn(mock(Map.class));
        assertThat(collectionUserService.getUserActions(user, null)).isNotNull();
    }

    @Test
    @DisplayName("Получение определенного действия пользователя")
    void getUserActions_SpecificAction() {
        User user = mock(User.class);
        when(user.getUserActions()).thenReturn(mock(Map.class));
        assertThat(collectionUserService.getUserActions(user, UserActionType.SIGN_IN)).isNotNull();
    }

}