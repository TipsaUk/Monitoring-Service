package ru.tipsauk.monitoring.service.in;

import lombok.Getter;
import lombok.Setter;
import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.model.UserAction;
import ru.tipsauk.monitoring.model.UserActionType;
import ru.tipsauk.monitoring.model.UserRole;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс сервиса для управления пользователями и их действиями.
 */
@Setter
@Getter
public class UserService {

    /** Коллекция, хранящая зарегистрированных пользователей. */
    private static Map<String, User> users = new HashMap<>();

    /** Авторизованный пользователь в текущей сессии. */
    private User sessionUser;

    static {
        users.put("admin", new User("admin"
            , "123", UserRole.ADMINISTRATOR));
    }

    /**
     * Регистрирует нового пользователя с указанным именем и паролем.
     *
     * @param nickName Имя пользователя.
     * @param password Пароль пользователя.
     * @return true, если регистрация прошла успешно, иначе false.
     */
    public boolean signUp(String nickName, String password) {
        if (users.containsKey(nickName)) {
           System.out.println("Пользователь с таким именем уже зарегистрирован!");
           return false;
        }
        if (password.equals("")) {
            System.out.println("Запрещен пустой пароль!");
            return false;
        }
        users.put(nickName, new User(nickName, password, UserRole.USER));
        return true;
    }

    /**
     * Выполняет вход пользователя в систему с указанным именем и паролем.
     *
     * @param name     Имя пользователя.
     * @param password Пароль пользователя.
     * @return true, если вход выполнен успешно, иначе false.
     */
    public boolean signIn(String name, String password) {
       User user = users.get(name);
        if (user == null) {
            System.out.println("Пользователь с таким именем не зарегистрирован!");
            return false;
        }
        if (!user.getPassword().equals(password)) {
            user.addUserAction(UserActionType.ERROR,"Не верный пароль!");
            return false;
        }
        sessionUser = user;
        user.addUserAction(UserActionType.SIGN_IN, "");
        return true;
    }

    /**
     * Выполняет выход текущего пользователя из системы.
     */
    public void signOut() {
        if (sessionUser == null) {
            System.out.println("Пользователь не авторизован!");
            return;
        }
        sessionUser.addUserAction(UserActionType.SIGN_OUT, "");
        sessionUser = null;
    }

    /**
     * Получает пользователя по имени.
     *
     * @param name Имя пользователя.
     * @return Пользователь с указанным именем или null, если не найден.
     */
    public User getUserByName(String name) {
        return users.get(name);
    }

    /**
     * Проверяет, является ли пользователь администратором.
     *
     * @param user Пользователь для проверки.
     * @return true, если пользователь является администратором, иначе false.
     */
    public boolean isUserAdministrator(User user) {
        return user.getRole() == UserRole.ADMINISTRATOR;
    }

    /**
     * Получает список всех зарегистрированных пользователей.
     *
     * @return Список всех пользователей.
     */
    public ArrayList<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    /**
     * Получает все действия пользователя в системе или действия определенного типа.
     *
     * @param user       Пользователь, действия которого получаются.
     * @param userAction Тип действия пользователя или null для получения всех действий.
     * @return Упорядоченное множество действий пользователя.
     */
    public TreeSet<UserAction> getUserActions(User user, UserActionType userAction) {
        TreeSet<UserAction> actions = new TreeSet<>(user.getUserActions().values());
        return actions.stream().filter(a -> userAction == null || a.getAction() == userAction)
                .collect(Collectors.toCollection(TreeSet::new));
    }

}
