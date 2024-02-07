package ru.tipsauk.monitoring.service.in.CollectionServicImpl;

import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.model.UserAction;
import ru.tipsauk.monitoring.model.UserActionType;
import ru.tipsauk.monitoring.model.UserRole;
import ru.tipsauk.monitoring.service.in.UserService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация интерфейса UserService с использованием коллекций.
 */
public class CollectionUserServiceImpl implements UserService {

    /** Коллекция, хранящая зарегистрированных пользователей. */
    private final Map<String, User> users = new HashMap<>();

    /** Авторизованный пользователь в текущей сессии. */
    private User sessionUser;

    {
        users.put("admin", new User("admin"
            , "123", UserRole.ADMINISTRATOR));
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
    public void signOut() {
        if (sessionUser == null) {
            System.out.println("Пользователь не авторизован!");
            return;
        }
        sessionUser.addUserAction(UserActionType.SIGN_OUT, "");
        sessionUser = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserByName(String name) {
        return users.get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<User> getAllUsers() {
        return new ArrayList<>(users.values());
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
        TreeSet<UserAction> actions = new TreeSet<>(user.getUserActions().values());
        return actions.stream().filter(a -> userAction == null || a.getAction() == userAction)
                .collect(Collectors.toCollection(TreeSet::new));
    }

}
