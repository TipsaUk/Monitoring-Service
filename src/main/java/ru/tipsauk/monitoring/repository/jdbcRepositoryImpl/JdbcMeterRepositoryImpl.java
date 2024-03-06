package ru.tipsauk.monitoring.repository.jdbcRepositoryImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.tipsauk.monitoring.model.Meter;
import ru.tipsauk.monitoring.repository.DatabaseService;
import ru.tipsauk.monitoring.repository.MeterRepository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Реализация интерфейса MeterRepository, с использованием взаимодействия с базой данных через JDBC.
 */

@Repository
@RequiredArgsConstructor
public class JdbcMeterRepositoryImpl implements MeterRepository {

    private final DatabaseService databaseService;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean saveMeter(Meter meter) {
        String query = "INSERT INTO monitoring.meter (name) VALUES (?)";
        try (PreparedStatement preparedStatement = databaseService.createPreparedStatement(query)) {
                preparedStatement.setString(1, meter.getName());
                preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка при записи счетчика: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Meter> getAllMeters() {
        Set<Meter> meters = new HashSet<>();
        try (PreparedStatement preparedStatement = databaseService.createPreparedStatement("SELECT * FROM monitoring.meter");
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Meter getMeterByName(String name) {
        Meter meter = null;
        String sql = "SELECT * FROM monitoring.meter WHERE name = ? ";
        try (PreparedStatement preparedStatement = databaseService.createPreparedStatement(sql)) {
            meter = resultSetMeterByName(name, preparedStatement);
        } catch (SQLException e) {
            System.out.println("Ошибка при получении счетчика: " + e.getMessage());
        }
        return meter;
    }

    /**
     * Возвращает объект Meter по имени счетчика, из базы данных с использованием
     * предварительно подготовленного запроса.
     *
     * @param nameMeter             Имя счетчика, по которому будет выполняться поиск в базе данных.
     * @param preparedStatement    Предварительно подготовленный запрос с параметрами для выполнения запроса.
     * @return Объект Meter, соответствующий указанному имени счетчика, или {@code null}, если счетчик не найден.
     * @throws SQLException        Если возникает ошибка при выполнении запроса к базе данных.
     */
    private Meter resultSetMeterByName(String nameMeter
            , PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, nameMeter);
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                return new Meter(id, name);
            }
        }
        return null;
    }
}
