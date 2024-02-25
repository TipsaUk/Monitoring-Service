package ru.tipsauk.monitoring.service.in.DatabaseServiceImpl;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.model.UserActionType;
import ru.tipsauk.monitoring.model.UserRole;
import ru.tipsauk.monitoring.service.in.UserService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class DatabaseUserServiceImplTest {

    @Container
    private static final PostgreSQLContainer<?> SQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("monitoring_service")
            .withUsername("monitoring")
            .withPassword("monitoring");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", SQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", SQLContainer::getPassword);
        registry.add("spring.datasource.username", SQLContainer::getUsername);
    }
    @Autowired
    private UserService userService;

    @BeforeAll
    static void setUp() {
        try  {
            YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
            yaml.setResources(new ClassPathResource("application-test.yml"));
            Properties properties = yaml.getObject();
            SQLContainer.start();
            Connection connection = DriverManager.getConnection(SQLContainer.getJdbcUrl()
                    , SQLContainer.getUsername(), SQLContainer.getPassword());
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase(properties.getProperty("spring.liquibase.change-log"),
                    new ClassLoaderResourceAccessor(), database);
            liquibase.update("test");
            connection.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    @DisplayName("Тест на успешную регистрацию пользователя")
    void signUp_SuccessfulRegistration() {
        assertThat(userService.signUp("testUser", "password")).isTrue();
        assertThat(userService.getAllUsers().size() > 1).isTrue();
    }

    @Test
    @DisplayName("Тест на невозможность дублирования пользователя")
    void signUp_UserAlreadyRegistered() {
        assertThat(userService.signUp("admin", "123")).isFalse();
    }

    @Test
    @DisplayName("Тест на запрет регистрации с пустым паролем")
    void signUp_EmptyPassword() {
        assertThat(userService.signUp("newUser", "")).isFalse();
    }

    @Test
    @DisplayName("Тест на запрет входа с неправильным логином")
    void signIn_UserNotFound() {
        assertThat(userService.signInWithSession("empty", "123")).isEqualTo(null);
    }

    @Test
    @DisplayName("Тест на запрет входа с неправильным паролем")
    void signIn_IncorrectPassword() {
        assertThat(userService.signInWithSession("admin", "wrong_password")).isEqualTo(null);
    }


    @Test
    @DisplayName("Тест на успешный поиск пользователя по логину")
    void getUserByName_UserExists() {
        User user = userService.getUserByName("admin");
        assertThat(user).isNotNull();
        assertThat(user.getNickName()).isEqualTo("admin");
    }

    @Test
    @DisplayName("Тест на неуспешный поиск пользователя по неверному логину")
    void getUserByName_UserNotFound() {
        User user = userService.getUserByName("empty");
        assertThat(user).isNull();
    }

    @Test
    @DisplayName("Тест на проверку прав администратора")
    void isUserAdministrator_AdminUser() {
        User adminUser = new User("admin", "123", UserRole.ADMINISTRATOR);
        assertThat(adminUser.isUserAdministrator()).isTrue();
    }

    @Test
    @DisplayName("Тест на проверку отсутствия прав администратора")
    void isUserAdministrator_NonAdminUser() {
        User regularUser = new User("user", "password", UserRole.USER);
        assertThat(regularUser.isUserAdministrator()).isFalse();
    }

    @Test
    @DisplayName("Тест на получение всех пользователей")
    void getAllUsers() {
        assertThat(userService.getAllUsers().size() > 0).isTrue();
    }

    @Test
    @DisplayName("Тест на получение всех действий пользователя")
    void getUserActions_AllActions() {
        User user = mock(User.class);
        assertThat(userService.getUserActions(user, null)).isNotNull();
    }

    @Test
    @DisplayName("Тест на получение определенного действия пользователя")
    void getUserActions_SpecificAction() {
        User user = mock(User.class);
        assertThat(userService.getUserActions(user, UserActionType.SIGN_IN)).isNotNull();
    }

}