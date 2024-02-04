package ru.tipsauk.monitoring.service.in.DatabaseServiceImpl;

import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.model.UserAction;
import ru.tipsauk.monitoring.model.UserActionType;
import ru.tipsauk.monitoring.model.UserRole;
import ru.tipsauk.monitoring.repository.UserActionRepository;
import ru.tipsauk.monitoring.repository.UserRepository;
import ru.tipsauk.monitoring.service.in.UserService;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Реализация интерфейса UserService с использованием базы данных.
 */
public class DatabaseUserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserActionRepository userActionRepository;

    /** Авторизованный пользователь в текущей сессии. */
    private User sessionUser;

    /**
     * Конструктор класса.
     *
     * @param userRepository        репозиторий пользователей.
     * @param userActionRepository репозиторий действий пользователей.
     */
    public DatabaseUserServiceImpl(UserRepository userRepository, UserActionRepository userActionRepository) {
        this.userRepository = userRepository;
        this.userActionRepository = userActionRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean signUp(String nickName, String password) {
        if (userRepository.getUserByName(nickName) != null) {
            System.out.println("Пользователь с таким именем уже зарегистрирован!");
            return false;
        }
        if (password.equals("")) {
            System.out.println("Запрещен пустой пароль!");
            return false;
        }
        userRepository.saveUser(new User(nickName, password, UserRole.USER));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean signIn(String name, String password) {
        User user = userRepository.getUserByName(name);
        if (user == null) {
            System.out.println("Пользователь с таким именем не зарегистрирован!");
            return false;
        }
        if (!user.getPassword().equals(password)) {
            user.addUserAction(UserActionType.ERROR,"Не верный пароль!");
            return false;
        }
        sessionUser = user;
        userActionRepository.saveUserAction(user, UserActionType.SIGN_IN, "");
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void signOut() {
        if (sessionUser == null) {
            System.out.println("Пользователь не авторизован!");
            return;
        }
        userActionRepository.saveUserAction(sessionUser, UserActionType.SIGN_OUT, "");
        sessionUser = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserByName(String name) {
        return userRepository.getUserByName(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getSessionUser() {
        return sessionUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreeSet<UserAction> getUserActions(User user, UserActionType userAction) {
        return userActionRepository.getByUserAndUserAction(user, userAction);
    }
}
