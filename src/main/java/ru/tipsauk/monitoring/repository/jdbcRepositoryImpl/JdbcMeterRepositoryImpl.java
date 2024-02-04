package ru.tipsauk.monitoring.repository.jdbcRepositoryImpl;

import ru.tipsauk.monitoring.config.ApplicationConfig;
import ru.tipsauk.monitoring.model.Meter;
import ru.tipsauk.monitoring.repository.MeterRepository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Реализация интерфейса MeterRepository, с использованием взаимодействия с базой данных через JDBC.
 */
public class JdbcMeterRepositoryImpl implements MeterRepository {

    private final String url;
    private final String username;
    private final String password;

    /**
     * Конструктор, принимающий объект конфигурации для настройки подключения к базе данных.
     *
     * @param config объект конфигурации.
     */
    public JdbcMeterRepositoryImpl(ApplicationConfig config) {
        this.url = config.getDbUrl();
        this.username = config.getDbUsername();
        this.password = config.getDbPassword();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveMeter(Meter meter) {

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "INSERT INTO monitoring.meter (name) VALUES (?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, meter.getName());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при записи счетчика: " + e.getMessage());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Meter> getAllMeters() {
        Set<Meter> meters = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM monitoring.meter");
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                meters.add(new Meter(id, name));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении счетчиков: " + e.getMessage());
        }
        return meters;
    }

}
