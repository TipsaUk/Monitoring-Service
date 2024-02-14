package ru.tipsauk.monitoring.servlet.in;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.tipsauk.monitoring.annotations.Loggable;
import ru.tipsauk.monitoring.handler.UserHandler;

import ru.tipsauk.monitoring.handler.mapper.UserActionMapper;
import ru.tipsauk.monitoring.handler.mapper.UserMapper;
import ru.tipsauk.monitoring.service.in.UserService;

import java.io.IOException;

/**
 * Класс Сервлет для обработки запросов, связанных с пользователями.
 * Этот сервлет обрабатывает запросы на получение действий пользователей и списка всех пользователей,
 * а также запросы на регистрацию, вход и выход из системы.
 */
@Loggable
public class UserServlet extends HttpServlet {

    /**
     * Обработчик запросов, связанных с пользователями.
     */
    private UserHandler userHandler;

    /**
     * Метод инициализации сервлета, вызывается при его создании.
     *
     * @param config Конфигурация сервлета.
     * @throws ServletException Исключение, возникающее при ошибках инициализации сервлета.
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        UserService userService = (UserService) config.getServletContext().getAttribute("userService");
        UserMapper userMapper = UserMapper.INSTANCE;
        UserActionMapper userActionMapper = UserActionMapper.INSTANCE;
        userHandler = new UserHandler(userService, userMapper, userActionMapper);

    }

    /**
     * Метод обрабатывает HTTP GET-запросы.
     *
     * @param req  Запрос от клиента.
     * @param resp Ответ сервера.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        switch (req.getPathInfo()) {
            case "/user_actions" -> userHandler.getUserActions(req, resp);
            case "/users" -> userHandler.getAllUsers(req, resp);
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Метод обрабатывает HTTP POST-запросы.
     *
     * @param req  Запрос от клиента.
     * @param resp Ответ сервера.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        switch (req.getPathInfo()) {
            case "/register" -> userHandler.signUp(req, resp);
            case "/login" -> userHandler.signIn(req, resp);
            case "/logout" -> userHandler.signOut(req, resp);
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
