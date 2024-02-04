package ru.tipsauk.monitoring.service.in.DatabaseServiceImpl;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tipsauk.monitoring.config.ApplicationConfig;
import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.model.UserActionType;
import ru.tipsauk.monitoring.model.UserRole;
import ru.tipsauk.monitoring.repository.UserActionRepository;
import ru.tipsauk.monitoring.repository.UserRepository;
import ru.tipsauk.monitoring.repository.jdbcRepositoryImpl.JdbcUserActionRepositoryImpl;
import ru.tipsauk.monitoring.repository.jdbcRepositoryImpl.JdbcUserRepositoryImpl;
import ru.tipsauk.monitoring.service.in.UserService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Testcontainers
class DatabaseUserServiceImplTest {

    @Container
    private static final PostgreSQLContainer<?> SQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("monitoring_service")
            .withUsername("monitoring")
            .withPassword("monitoring");

    private static UserService userService;

    @BeforeAll
    static void setUp() {
        try  {
            SQLContainer.start();
            Properties liquibaseProperties = new Properties();
            liquibaseProperties.load(DatabaseMeterServiceImplTest.class
                    .getClassLoader().getResourceAsStream("application.properties"));
            Connection connection = DriverManager.getConnection(SQLContainer.getJdbcUrl()
                    , SQLContainer.getUsername(), SQLContainer.getPassword());
            String changeLogFile = liquibaseProperties.getProperty("changeLogFile");
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase(changeLogFile,
                    new ClassLoaderResourceAccessor(), database);
            liquibase.update("test");
            connection.close();

            ApplicationConfig config = new ApplicationConfig(SQLContainer.getJdbcUrl()
                    , SQLContainer.getUsername(), SQLContainer.getPassword());
            UserRepository userRepository = new JdbcUserRepositoryImpl(config);
            UserActionRepository userActionRepository = new JdbcUserActionRepositoryImpl(config);
            userService = new DatabaseUserServiceImpl(userRepository, userActionRepository);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    @DisplayName("Регистрация пользователя")
    void signUp_SuccessfulRegistration() {
        assertThat(userService.signUp("testUser", "password")).isTrue();
        assertThat(userService.getAllUsers().size() > 1).isTrue();
    }

    @Test
    @DisplayName("Дублирование пользователя")
    void signUp_UserAlreadyRegistered() {
        assertThat(userService.signUp("admin", "123")).isFalse();
    }

    @Test
    @DisplayName("Пустой пароль")
    void signUp_EmptyPassword() {
        assertThat(userService.signUp("newUser", "")).isFalse();
    }

    @Test
    @DisplayName("Вход пользователя в систему")
    void signIn_SuccessfulSignIn() {
        assertThat(userService.signIn("admin", "123")).isTrue();
        assertThat(userService.getSessionUser()).isNotNull();
    }

    @Test
    @DisplayName("Вход с неправильным логином")
    void signIn_UserNotFound() {
        assertThat(userService.signIn("empty", "123")).isFalse();
    }

    @Test
    @DisplayName("Вход с неправильным паролем")
    void signIn_IncorrectPassword() {
        assertThat(userService.signIn("admin", "wrong_password")).isFalse();
    }

    @Test
    @DisplayName("Выход пользователя из системы")
    void signOut_SuccessfulSignOut() {
        userService.signIn("admin", "123");
        userService.signOut();
        assertThat(userService.getSessionUser()).isNull();
    }

    @Test
    @DisplayName("Поиск пользователя по логину")
    void getUserByName_UserExists() {
        User user = userService.getUserByName("admin");
        assertThat(user).isNotNull();
        assertThat(user.getNickName()).isEqualTo("admin");
    }

    @Test
    @DisplayName("Поиск пользователя по неверному логину")
    void getUserByName_UserNotFound() {
        User user = userService.getUserByName("empty");
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
        assertThat(userService.getAllUsers().size() > 0).isTrue();
    }

    @Test
    @DisplayName("Получение всех действий пользователя")
    void getUserActions_AllActions() {
        User user = mock(User.class);
        when(user.getUserActions()).thenReturn(mock(Map.class));
        assertThat(userService.getUserActions(user, null)).isNotNull();
    }

    @Test
    @DisplayName("Получение определенного действия пользователя")
    void getUserActions_SpecificAction() {
        User user = mock(User.class);
        when(user.getUserActions()).thenReturn(mock(Map.class));
        assertThat(userService.getUserActions(user, UserActionType.SIGN_IN)).isNotNull();
    }

}