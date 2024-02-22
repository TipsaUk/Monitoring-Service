package ru.tipsauk.monitoring.service.in;

import ru.tipsauk.monitoring.dto.UserActionDto;
import ru.tipsauk.monitoring.dto.UserDto;
import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.model.UserActionType;

import java.util.Set;
import java.util.TreeSet;

/**
 * Интерфейс сервиса для управления пользователями и их действиями.
 */
public interface UserService {

    /**
     * Регистрирует нового пользователя с указанным никнеймом и паролем.
     *
     * @param nickName никнейм нового пользователя.
     * @param password пароль нового пользователя.
     * @return true, если регистрация успешна, в противном случае - false.
     */
    boolean signUp(String nickName, String password);

    /**
     * Авторизует пользователя с указанным именем и паролем для web-реализации.
     *
     * @param name     имя пользователя для авторизации.
     * @param password пароль пользователя для авторизации.
     * @return true, если авторизация успешна, в противном случае - false.
     */
    String signInWithSession(String name, String password);

    /**
     * Выход из системы по id текущей сессии (для реализации web).
     */
    boolean signOutBySessionId(String sessionId);

    /**
     * Получает пользователя по его имени.
     *
     * @param name имя пользователя.
     * @return объект User, представляющий пользователя с указанным именем.
     */
    User getUserByName(String name);

    /**
     * Получает пользователя по id сессии.
     *
     * @param sessionId id сессии.
     * @return объект User, представляющий пользователя с указанным именем.
     */
    User getUserBySessionId(String sessionId);

    /**
     * Получает id сесии по имени пользователя.
     *
     * @param name имя пользователя.
     * @return объект String, id сесии для указанного пользователя.
     */
    String getUserSessionId(String name);

    /**
     * Получает список всех зарегистрированных пользователей.
     *
     * @return ArrayList объектов User, представляющих всех зарегистрированных пользователей.
     */
    Set<UserDto> getAllUsers();


    /**
     * Получает действия пользователя по заданному типу.
     *
     * @param user       пользователь, для которого получаются действия.
     * @param userAction тип действия пользователя или null - без отбора.
     * @return TreeSet объектов UserAction, представляющих действия пользователя указанного типа.
     */
    TreeSet<UserActionDto> getUserActions(User user, UserActionType userAction);

}
