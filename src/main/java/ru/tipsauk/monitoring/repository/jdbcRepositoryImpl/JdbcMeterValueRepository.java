package ru.tipsauk.monitoring.repository.jdbcRepositoryImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.tipsauk.monitoring.model.Meter;
import ru.tipsauk.monitoring.model.MeterValue;
import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.repository.DatabaseService;
import ru.tipsauk.monitoring.repository.MeterValueRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TreeSet;

/**
 * Реализация интерфейса MeterValueRepository, с использованием взаимодействия с базой данных через JDBC.
 */
@Repository
@RequiredArgsConstructor
public class JdbcMeterValueRepository implements MeterValueRepository {

    private final DatabaseService databaseService;

    /**
     * {@inheritDoc}
     */
    @Override
    public MeterValue getLastValueMeter(User user) {
        MeterValue meterValue = null;
        String sql = "SELECT mv.*, m.name AS name_meter " +
                "FROM monitoring.meter_value mv " +
                "JOIN monitoring.meter m ON mv.meter_id = m.id " +
                "WHERE mv.user_id = ? " +
                "AND mv.date_value = (SELECT MAX(date_value) FROM monitoring.meter_value " +
                "                   WHERE user_id = mv.user_id) " +
                "ORDER BY mv.date_value DESC";
        try (PreparedStatement preparedStatement = databaseService.createPreparedStatement(sql)) {
            meterValue = resultSetValueMeter(user, preparedStatement);
        } catch (SQLException e) {
            System.out.println("Ошибка при получении актуальных записей счетчика: " + e.getMessage());
        }
        return meterValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MeterValue getValueMeterByDateAndUser(LocalDate dateValue, User user) {
        MeterValue meterValue = new MeterValue(dateValue);
        String sql = "SELECT mv.*, m.name AS name_meter " +
                    "FROM monitoring.meter_value mv " +
                    "JOIN monitoring.meter m ON mv.meter_id = m.id " +
                    "WHERE mv.date_value = ? AND mv.user_id = ? " +
                    "ORDER BY mv.date_value DESC ";
            try (PreparedStatement preparedStatement = databaseService.createPreparedStatement(sql)) {
                meterValue = resultSetValueMeterByDateAndUser(dateValue, user, preparedStatement);
        } catch (SQLException e) {
            System.out.println("Ошибка при получении записей счетчика: " + e.getMessage());
        }
        return meterValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean saveMeterValue(Meter meter, int value, User user) {
        String sql = "INSERT INTO meter_value (date_value, meter_id, user_id, value) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = databaseService.createPreparedStatement(sql)) {
            LocalDateTime currentDateTime = LocalDateTime.now().withDayOfMonth(1);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(currentDateTime));
            preparedStatement.setLong(2, meter.getId());
            preparedStatement.setLong(3, user.getId());
            preparedStatement.setInt(4, value);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка при записи показаний счетчика: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreeSet<MeterValue> getAllMeterValuesOrderedByDateValue(User user) {
        TreeSet<MeterValue> meterValues = new TreeSet<>();
        String sql = "SELECT mv.*, m.name AS name_meter " +
                "FROM monitoring.meter_value mv " +
                "JOIN monitoring.meter m ON mv.meter_id = m.id " +
                "WHERE mv.user_id = ? " +
                "ORDER BY mv.date_value DESC ";
        try (PreparedStatement preparedStatement = databaseService.createPreparedStatement(sql)) {
            meterValues = resultSetAllMeterValues(user, preparedStatement);
        } catch (SQLException e) {
            System.out.println("Ошибка при получении записей счетчиков: " + e.getMessage());
        }
        return meterValues;
    }

    /**
     * Получает показания счетчиков MeterValue по дате и пользователю из базы данных с использованием
     * предварительно подготовленного запроса.
     *
     * @param dateValue             Дата, по которой необходимо получить данные о показаниях счетчиков.
     * @param user                  Пользователь, чьи данные о показаниях счетчиков требуется получить.
     * @param preparedStatement    Предварительно подготовленный запрос с параметрами для выполнения запроса.
     * @return Объект MeterValue, содержащий данные о показаниях счетчиков для указанной даты и пользователя.
     */
    private MeterValue resultSetValueMeterByDateAndUser(LocalDate dateValue, User user
            , PreparedStatement preparedStatement) throws SQLException {
        MeterValue meterValue = new MeterValue(dateValue);
        preparedStatement.setDate(1, Date.valueOf(dateValue));
        preparedStatement.setLong(2, user.getId());

        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                long meterId = resultSet.getLong("meter_id");
                int value = resultSet.getInt("value");
                String meterName = resultSet.getString("name_meter");
                meterValue.addMeterReading(new Meter(meterId, meterName), value);
            }
        }
        return meterValue;
    }

    /**
     * Получает актуальные показания счетчиков MeterValue для пользователя из базы данных с использованием
     * предварительно подготовленного запроса.
     *
     * @param user                  Пользователь, чьи данные о показаниях счетчиков требуется получить.
     * @param preparedStatement    Предварительно подготовленный запрос с параметрами для выполнения запроса.
     * @return Объект MeterValue, содержащий данные о показаниях счетчиков для указанного пользователя.
     */
    private MeterValue resultSetValueMeter(User user
            , PreparedStatement preparedStatement) throws SQLException {
        MeterValue meterValue = null;
        preparedStatement.setLong(1, user.getId());
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                java.sql.Timestamp timestamp = resultSet.getTimestamp("date_value");
                if (meterValue == null) {
                    meterValue = new MeterValue(timestamp.toLocalDateTime().toLocalDate());
                }
                long meterId = resultSet.getLong("meter_id");
                int value = resultSet.getInt("value");
                String meterName = resultSet.getString("name_meter");
                meterValue.addMeterReading(new Meter(meterId, meterName), value);
            }
        }
        return meterValue;
    }

    /**
     * Получает из базы данных для пользователя историю передачи показаний счетчика  с использованием
     * предварительно подготовленного запроса. Каждый объект MeterValue представляет собой данные за определенную месяц.
     *
     * @param user                  Пользователь, чьи данные о показаниях счетчиков требуется получить.
     * @param preparedStatement    Предварительно подготовленный запрос с параметрами для выполнения запроса.
     * @return Набор объектов MeterValue, содержащих данные о показаниях счетчиков для указанного пользователя.
     */
    private TreeSet<MeterValue> resultSetAllMeterValues(User user
            , PreparedStatement preparedStatement) throws SQLException {
        MeterValue meterValue = null;
        TreeSet<MeterValue> meterValues = new TreeSet<>();
        preparedStatement.setLong(1, user.getId());
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                java.sql.Timestamp timestamp = resultSet.getTimestamp("date_value");
                LocalDate dateValue = timestamp.toLocalDateTime().toLocalDate();
                if (meterValue == null || !dateValue.equals(meterValue.getDateValue())) {
                    meterValue = new MeterValue(dateValue);
                }
                long meterId = resultSet.getLong("meter_id");
                int value = resultSet.getInt("value");
                String meterName = resultSet.getString("name_meter");
                meterValue.addMeterReading(new Meter(meterId, meterName), value);
                meterValues.add(meterValue);
            }
        }
        return meterValues;
    }
}
