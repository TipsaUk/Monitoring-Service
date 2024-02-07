package ru.tipsauk.monitoring.repository;

import ru.tipsauk.monitoring.model.User;

import java.util.ArrayList;

/**
 * Интерфейс для взаимодействия с базой данных по данным пользователей.
 */
public interface UserRepository {

    /**
     * Получает пользователя по его никнейму (имени).
     *
     * @param nickName никнейм пользователя.
     * @return объект User или null, если пользователь не найден.
     */
    User getUserByName(String nickName);

    /**
     * Сохраняет нового пользователя в БД.
     *
     * @param user новый пользователь для сохранения.
     */
    void saveUser(User user);

    /**
     * Получает список всех пользователей из БД.
     *
     * @return список всех пользователей.
     */
    ArrayList<User> getAllUsers();
}
