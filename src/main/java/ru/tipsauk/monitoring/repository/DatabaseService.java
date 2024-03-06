package ru.tipsauk.monitoring.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Компонент для управления подключением к базе данных.
 */
@Component
public class DatabaseService {

    private Connection connection;

    /**
     * Имя пользователя для подключения к базе данных.
     */
    @Value("${spring.datasource.username}")
    private String username;

    /**
     * Пароль для подключения к базе данных.
     */
    @Value("${spring.datasource.password}")
    private String password;

    /**
     * URL для подключения к базе данных.
     */
    @Value("${spring.datasource.url}")
    private String url;

    /**
     * Получение соединения с базой данных.
     *
     * @return Соединение с базой данных.
     * @throws SQLException Если произошла ошибка при установке соединения.
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }

    /**
     * Закрытие соединения с базой данных.
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Получение подготовленного выражения для SQL-запроса.
     *
     * @param sql SQL-запрос.
     * @return PreparedStatement - подготовленное выражение.
     * @throws SQLException Если произошла ошибка при подготовке выражения.
     */
    public PreparedStatement createPreparedStatement(String sql) throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Ошибка инициализации драйвера: " + e.getMessage());
        }
        Connection connection = getConnection();
        return connection.prepareStatement(sql);
    }

}
