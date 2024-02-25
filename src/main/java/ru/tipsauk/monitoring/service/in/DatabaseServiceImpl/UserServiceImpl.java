package ru.tipsauk.monitoring.service.in.DatabaseServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tipsauk.monitoring.dto.UserActionDto;
import ru.tipsauk.monitoring.dto.UserDto;
import ru.tipsauk.monitoring.mapper.UserActionMapper;
import ru.tipsauk.monitoring.mapper.UserMapper;
import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.model.UserActionType;
import ru.tipsauk.monitoring.model.UserRole;
import ru.tipsauk.monitoring.repository.UserActionRepository;
import ru.tipsauk.monitoring.repository.UserRepository;
import ru.tipsauk.monitoring.service.in.UserService;
import ru.tipsauk.starter_logger.annotations.Loggable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация интерфейса UserService с использованием базы данных для web-версии приложения.
 */

@Loggable
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserActionRepository userActionRepository;

    /** Авторизованный пользователи системы. */
    private final Map<String, User> userSessions = new HashMap<>();

    /**
     * Маппер для преобразования объектов User в объекты DTO (Data Transfer Object) и наоборот.
     */
    private final UserMapper userMapper;

    /**
     * Маппер для преобразования объектов UserAction в объекты DTO (Data Transfer Object) и наоборот.
     */
    private final UserActionMapper userActionMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean signUp(String nickName, String password) {
        if (hasSystemUserName(nickName)) {
            return false;
        }
        if (password.isEmpty()) {
            log.info("Запрещен пустой пароль!");
            return false;
        }
        userRepository.saveUser(new User(nickName, password, UserRole.USER));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String signInWithSession(String name, String password) {
        User user = userRepository.getUserByName(name);
        if (user == null) {
            log.info("Пользователь с таким именем не зарегистрирован!");
            return null;
        }
        if (!user.getPassword().equals(password)) {
            log.info("Не верный пароль!");
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
    public Set<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(userMapper::userToUserDto)
                .collect(Collectors.toSet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreeSet<UserActionDto> getUserActions(User user, UserActionType userAction) {
        return userActionRepository.getByUserAndUserAction(user, userAction).stream()
                .map(userActionMapper::userActionToUserActionDto).collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean saveUserAction(String userSessionId, String userAction, String description) {
        User user = getUserBySessionId(userSessionId);
        if (user == null) {
            return false;
        }
        userActionRepository.saveUserAction(user, UserActionType.valueOf(userAction), description);
        return true;
    }

    /**
     * Проверяет наличие пользователя по имени.
     *
     * @param nickName Никнейм пользователя для проверки.
     * @return {@code true}, если пользователь с указанным никнеймом существует, иначе {@code false}.
     */
    private boolean hasSystemUserName(String nickName) {
        return userRepository.getUserByName(nickName) != null;
    }
}
