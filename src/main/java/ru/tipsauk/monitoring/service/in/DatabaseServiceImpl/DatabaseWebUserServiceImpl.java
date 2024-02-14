package ru.tipsauk.monitoring.service.in.DatabaseServiceImpl;

import ru.tipsauk.monitoring.annotations.UserAudit;
import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.model.UserAction;
import ru.tipsauk.monitoring.model.UserActionType;
import ru.tipsauk.monitoring.model.UserRole;
import ru.tipsauk.monitoring.repository.UserActionRepository;
import ru.tipsauk.monitoring.repository.UserRepository;
import ru.tipsauk.monitoring.service.in.UserService;

import java.util.*;

/**
 * Реализация интерфейса UserService с использованием базы данных для web-версии приложения.
 */
public class DatabaseWebUserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserActionRepository userActionRepository;

    /** Авторизованный пользователи системы. */
    private final Map<String, User> userSessions = new HashMap<>();

    /**
     * Конструктор класса.
     *
     * @param userRepository        репозиторий пользователей.
     * @param userActionRepository репозиторий действий пользователей.
     */
    public DatabaseWebUserServiceImpl(UserRepository userRepository, UserActionRepository userActionRepository) {
         this.userRepository = userRepository;
         this.userActionRepository = userActionRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean signUp(String nickName, String password) {
        if (hasSystemUserName(nickName)) {
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
       // не используется в данной реализации
        return true;
    }

    @Override
    public String signInWithSession(String name, String password) {
        User user = userRepository.getUserByName(name);
        if (user == null) {
            System.out.println("Пользователь с таким именем не зарегистрирован!");
            return null;
        }
        if (!user.getPassword().equals(password)) {
            user.addUserAction(UserActionType.ERROR,"Не верный пароль!");
            return null;
        }
        String sessionId = UUID.randomUUID().toString();
        userSessions.put(sessionId, user);
        return sessionId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void signOut() {
        // не используется в данной реализации
    }

    @Override
    public boolean signOutBySessionId(String sessionId) {
        if (!userSessions.containsKey(sessionId)) {
            return false;
        }
        userSessions.remove(sessionId);
        return true;
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
    public User getUserBySessionId(String sessionId) {
        return userSessions.get(sessionId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserSessionId(String name) {
        Optional<String> matchingKey = userSessions.entrySet().stream()
                .filter(entry -> name.equals(entry.getValue().getNickName()))
                .map(Map.Entry::getKey)
                .findFirst();
        return matchingKey.orElse(null);
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
        // не используется в данной реализации
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreeSet<UserAction> getUserActions(User user, UserActionType userAction) {
        return userActionRepository.getByUserAndUserAction(user, userAction);
    }

    private boolean hasSystemUserName(String nickName) {
        if (userRepository.getUserByName(nickName) != null) {
            System.out.println("Пользователь с таким именем уже зарегистрирован!");
            return true;
        }
        return false;
    }
}
