package ru.tipsauk.monitoring.repository.jdbcRepositoryImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.model.UserRole;
import ru.tipsauk.monitoring.repository.DatabaseService;
import ru.tipsauk.monitoring.repository.UserRepository;

import java.sql.*;
import java.util.ArrayList;

/**
 * Реализация интерфейса UserRepository, с использованием взаимодействия с базой данных через JDBC.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcUserRepositoryImpl implements UserRepository {

    private final DatabaseService databaseService;

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserByName(String nickName) {
        User user = null;
        String sql = "SELECT * FROM monitoring.user WHERE nic_name = ? ";
        try (PreparedStatement preparedStatement = databaseService.createPreparedStatement(sql)) {
            user = resultSetUserByName(nickName, preparedStatement);
        } catch (SQLException e) {
            log.info("Ошибка при получении пользователя: " + e.getMessage());
        }
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveUser(User user) {
        String sql = "INSERT INTO monitoring.user (nic_name, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = databaseService.createPreparedStatement(sql)) {
            preparedStatement.setString(1, user.getNickName());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getRole().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.info("Ошибка при записи пользователя: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        String sql = "SELECT * FROM monitoring.user";
        try (PreparedStatement preparedStatement = databaseService.createPreparedStatement(sql)) {
            users = resultSetAllUsers(preparedStatement);
        } catch (SQLException e) {
            log.info("Ошибка при получении пользователя: " + e.getMessage());
        }
        return users;
    }


    /**
     * Получает пользователя по никнейму из базы данных по подготовленному запросу.
     *
     * @param nickName              Никнейм пользователя, по которому будет выполняться поиск в базе данных.
     * @param preparedStatement    Предварительно подготовленный запрос с параметрами для выполнения запроса.
     * @return Объект пользователя, соответствующий указанному никнейму, или {@code null}, если пользователь не найден.
     */
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


    /**
     * Возвращает список всех пользователей из базы данных
     * с использованием предварительно подготовленного запроса.
     *
     * @param preparedStatement    Предварительно подготовленный запрос с параметрами для выполнения запроса.
     * @return Список объектов пользователей, содержащих данные о всех пользователях в базе данных.
     */
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
