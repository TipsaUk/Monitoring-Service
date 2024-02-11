package ru.tipsauk.monitoring.servlet.in;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.tipsauk.monitoring.config.ApplicationConfig;
import ru.tipsauk.monitoring.handler.util.RequestUtils;
import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.repository.UserActionRepository;
import ru.tipsauk.monitoring.repository.UserRepository;
import ru.tipsauk.monitoring.repository.jdbcRepositoryImpl.JdbcUserActionRepositoryImpl;
import ru.tipsauk.monitoring.repository.jdbcRepositoryImpl.JdbcUserRepositoryImpl;
import ru.tipsauk.monitoring.service.in.DatabaseServiceImpl.DatabaseWebUserServiceImpl;
import ru.tipsauk.monitoring.service.in.UserService;


import java.io.IOException;

/**
 * Фильтр авторизации (AuthorizationFilter) предназначен для проверки авторизации пользователя
 * при обращении к ресурсам. Если пользователь не авторизован, ему будет возвращен
 * статус HTTP_UNAUTHORIZED (401). Ресурсы, связанные с регистрацией и входом в систему,
 * исключены из проверки.
 */
public class AuthorizationFilter  implements Filter {

    /**
     * Сервис пользователей для проверки авторизации.
     */
    private UserService userService;

    /**
     * Метод инициализации фильтра, вызывается при его создании.
     *
     * @param filterConfig Конфигурация фильтра.
     * @throws ServletException Исключение, возникающее при ошибках инициализации фильтра.
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ApplicationConfig appConfig = new ApplicationConfig();
        UserActionRepository userActionRepository = new JdbcUserActionRepositoryImpl(appConfig);
        UserRepository userRepository = new JdbcUserRepositoryImpl(appConfig);
        userService = new DatabaseWebUserServiceImpl(userRepository, userActionRepository);
        filterConfig.getServletContext().setAttribute("userService", userService);
        if (userService == null) {
            throw new ServletException("UserService is not found in the ServletContext");
        }
    }

    /**
     * Метод выполнения фильтрации запроса. Проверяет авторизацию пользователя при обращении
     * к ресурсам. Если пользователь не авторизован, возвращается статус HTTP_UNAUTHORIZED.
     *
     * @param request  Запрос от клиента.
     * @param response Ответ сервера.
     * @param chain    Цепочка фильтров.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        if ("/user/register".equals(path) || "/user/login".equals(path)) {
            chain.doFilter(request, response);
            return;
        }
        User currentUser = userService.getUserBySessionId(RequestUtils.getCurrentSessionId(httpRequest));
        if (currentUser != null) {
            chain.doFilter(request, response);
        } else {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
