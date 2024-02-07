package ru.tipsauk.monitoring.repository.jdbcRepositoryImpl;

import ru.tipsauk.monitoring.config.ApplicationConfig;
import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.model.UserRole;
import ru.tipsauk.monitoring.repository.UserRepository;

import java.sql.*;
import java.util.ArrayList;

/**
 * Реализация интерфейса UserRepository, с использованием взаимодействия с базой данных через JDBC.
 */
public class JdbcUserRepositoryImpl implements UserRepository {

    private final String url;
    private final String username;
    private final String password;

    /**
     * Конструктор, принимающий объект конфигурации для настройки подключения к базе данных.
     *
     * @param config объект конфигурации.
     */
    public JdbcUserRepositoryImpl(ApplicationConfig config) {
        this.url = config.getDbUrl();
        this.username = config.getDbUsername();
        this.password = config.getDbPassword();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserByName(String nickName) {
        User user = null;
        String sql = "SELECT * FROM monitoring.user WHERE nic_name = ? ";
        try (PreparedStatement preparedStatement = setPreparedStatement(sql)) {
            user = resultSetUserByName(nickName, preparedStatement);
        } catch (SQLException e) {
            System.out.println("Ошибка при получении пользователя: " + e.getMessage());
        }
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveUser(User user) {
        String sql = "INSERT INTO monitoring.user (nic_name, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = setPreparedStatement(sql)) {
            preparedStatement.setString(1, user.getNickName());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getRole().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка при записи пользователя: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        String sql = "SELECT * FROM monitoring.user";
        try (PreparedStatement preparedStatement = setPreparedStatement(sql)) {
            users = resultSetAllUsers(preparedStatement);
        } catch (SQLException e) {
            System.out.println("Ошибка при получении пользователя: " + e.getMessage());
        }
        return users;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public PreparedStatement setPreparedStatement(String sql) throws SQLException {
        Connection connection = getConnection();
        return connection.prepareStatement(sql);
    }

    private User resultSetUserByName(String nickName
            , PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, nickName);
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                long userId = resultSet.getLong("id");
                String password = resultSet.getString("password");
                String role = resultSet.getString("role");
                return new User(userId, nickName, password, UserRole.valueOf(role));
            }
        }
        return null;
    }

    private ArrayList<User> resultSetAllUsers(PreparedStatement preparedStatement) throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                long userId = resultSet.getLong("id");
                String nickName = resultSet.getString("nic_name");
                String password = resultSet.getString("password");
                String role = resultSet.getString("role");
                users.add(new User(userId, nickName, password, UserRole.valueOf(role)));
            }
        }
        return users;
    }
}
