package ru.tipsauk.monitoring.service.in;

import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.model.UserAction;
import ru.tipsauk.monitoring.model.UserActionType;

import java.util.ArrayList;
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
     * Авторизует пользователя с указанным именем и паролем.
     *
     * @param name     имя пользователя для авторизации.
     * @param password пароль пользователя для авторизации.
     * @return true, если авторизация успешна, в противном случае - false.
     */
    boolean signIn(String name, String password);

    /**
     * Выход из системы для текущего пользователя.
     */
    void signOut();

    /**
     * Получает пользователя по его имени.
     *
     * @param name имя пользователя.
     * @return объект User, представляющий пользователя с указанным именем.
     */
    User getUserByName(String name);

    /**
     * Получает список всех зарегистрированных пользователей.
     *
     * @return ArrayList объектов User, представляющих всех зарегистрированных пользователей.
     */
    ArrayList<User> getAllUsers();

    /**
     * Получает текущего пользователя сессии.
     *
     * @return объект User, представляющий текущего пользователя сессии.
     */
    User getSessionUser();

    /**
     * Получает действия пользователя по заданному типу.
     *
     * @param user       пользователь, для которого получаются действия.
     * @param userAction тип действия пользователя или null - без отбора.
     * @return TreeSet объектов UserAction, представляющих действия пользователя указанного типа.
     */
    TreeSet<UserAction> getUserActions(User user, UserActionType userAction);

}
