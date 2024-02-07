package ru.tipsauk.monitoring.console;

import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.service.in.UserService;

import java.util.Scanner;

/**
 * Класс, представляющий действия пользователя в консоли.
 */
public class ConsoleUserActions {

    /**
     * Сервис для работы с пользователями.
     */
    private final UserService userService;

    /**
     * Сканер для ввода с консоли.
     */
    private final Scanner scanner;

    /**
     * Конструктор класса ConsoleUserActions.
     *
     * @param userService Сервис для работы с пользователями.
     * @param scanner     Сканер для ввода с консоли.
     */
    public ConsoleUserActions(UserService userService, Scanner scanner) {
        this.userService = userService;
        this.scanner = scanner;
    }

    /**
     * Метод для регистрации пользователя в консоли.
     */
    public void consoleSignUp() {
        System.out.println("Введите имя пользователя:");
        String nickName = scanner.next();
        if (nickName.isEmpty()) {
            System.out.println("Имя пользователя не может быть пустым.");
            return;
        }
        System.out.println("Введите пароль:");
        String password = scanner.next();
        if (password.isEmpty()) {
            System.out.println("Пароль не может быть пустым.");
            return;
        }
        if (userService.signUp(nickName, password)) {
            System.out.println("Регистрация успешна!");
        }
    }

    /**
     * Метод для входа в систему пользователя в консоли.
     */
    public void consoleSignIn() {
        System.out.println("Введите имя пользователя:");
        String nickName = scanner.next();
        System.out.println("Введите пароль:");
        String password = scanner.next();
        if (userService.signIn(nickName, password)) {
            System.out.println("Вход выполнен успешно!");
        }
    }

    /**
     * Метод для выхода из системы пользователя в консоли.
     */
    public void consoleSignOut() {
        userService.signOut();
        System.out.println("Выход выполнен успешно!");
    }

     /**
     * Метод получение действий пользователя в системе (только для администратора).
     *
     * @param currentUser текущий пользователь консоли
     */
     public void consoleGetUserActions(User currentUser) {
        if (!currentUser.isUserAdministrator()) {
            return;
        }
        userService.getUserActions(consoleInputUser(), null)
                .forEach(a->System.out.println(a.getTimeAction() + " -> " + a.getAction() + " " + a.getDescription()));
    }

    public User getUserForGettingInformation() {
        User currentUser = userService.getSessionUser();
        return (currentUser.isUserAdministrator()) ? consoleInputUser() : currentUser;
    }

    /**
     * Метод для выбора пользователя в консоли (только для администратора).
     *
     * @return выбранный пользователь.
     */
    private User consoleInputUser() {
        System.out.println("Пользователи:");
        userService.getAllUsers().forEach(u ->System.out.println(u.getNickName()));
        System.out.println("Введите имя пользователя для получения данных:");
        while (true) {
            String nicName = scanner.next();
            User user = userService.getUserByName(nicName);
            if (user != null) {
                return user;
            }
            System.out.println("Пользователя с таким именем не существует!");
        }
    }

    /**
     * Метод для получения текущего пользователя в сессии.
     *
     * @return Текущий пользователь сессии.
     */
    public User getSessionUser() {
        return userService.getSessionUser();
    }
}
