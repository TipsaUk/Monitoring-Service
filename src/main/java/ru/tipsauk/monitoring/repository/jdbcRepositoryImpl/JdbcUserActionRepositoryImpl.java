package ru.tipsauk.monitoring.repository.jdbcRepositoryImpl;

import ru.tipsauk.monitoring.config.ApplicationConfig;
import ru.tipsauk.monitoring.model.*;
import ru.tipsauk.monitoring.repository.UserActionRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.TreeSet;

/**
 * Реализация интерфейса UserActionRepository, с использованием взаимодействия с базой данных через JDBC.
 */
public class JdbcUserActionRepositoryImpl implements UserActionRepository {

    private final String url;
    private final String username;
    private final String password;

    /**
     * Конструктор, принимающий объект конфигурации для настройки подключения к базе данных.
     *
     * @param config объект конфигурации.
     */
    public JdbcUserActionRepositoryImpl(ApplicationConfig config) {
        this.url = config.getDbUrl();
        this.username = config.getDbUsername();
        this.password = config.getDbPassword();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveUserAction(User user, UserActionType actionType, String description) {
        String sql = "INSERT INTO monitoring.user_action (time_action, user_id, action, description) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = setPreparedStatement(sql)) {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setLong(2, user.getId());
            preparedStatement.setString(3, actionType.toString());
            preparedStatement.setString(4, description);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка при записи действия пользователя: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreeSet<UserAction> getByUserAndUserAction(User user, UserActionType userAction) {
        TreeSet<UserAction> userActions = new TreeSet<>();
        String sql = "SELECT * FROM monitoring.user_action " +
                "WHERE user_id = ? " +
                (userAction == null ? "" : "AND action = ? ") +
                "ORDER BY time_action DESC ";
        try (PreparedStatement preparedStatement = setPreparedStatement(sql)) {
            userActions = resultSetUserAndUserAction(user, userAction, preparedStatement);
        } catch (SQLException e) {
            System.out.println("Ошибка при получении действия пользователя: " + e.getMessage());
        }
        return userActions;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public PreparedStatement setPreparedStatement(String sql) throws SQLException {
        Connection connection = getConnection();
        return connection.prepareStatement(sql);
    }

    private TreeSet<UserAction> resultSetUserAndUserAction(User user, UserActionType actionType
            , PreparedStatement preparedStatement) throws SQLException {
        TreeSet<UserAction> userActions = new TreeSet<>();
        preparedStatement.setLong(1, user.getId());
        if (actionType != null) {
            preparedStatement.setString(2, actionType.toString());
        }
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                java.sql.Timestamp timestamp = resultSet.getTimestamp("time_action");
                LocalDateTime timeAction = timestamp.toLocalDateTime();
                String action = resultSet.getString("action");
                String description = resultSet.getString("description");
                userActions.add(new UserAction(timeAction, UserActionType.valueOf(action), description));
            }
        }
        return userActions;
    }
}
