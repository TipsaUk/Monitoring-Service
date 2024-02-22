package ru.tipsauk.monitoring.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import ru.tipsauk.monitoring.util.RequestUtils;
import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.service.in.UserService;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Компонент - интерцептор для авторизации запросов.
 */
@Component
@RequiredArgsConstructor
public class AuthorizationInterceptor implements HandlerInterceptor {

    /**
     * Сервис пользователя для выполнения операций связанных с пользователями.
     */
    private final UserService userService;

    /**
     * Множество URL-путей, которые игнорируются при проверке авторизации.
     */
    private final Set<String> ignoreMethods = new HashSet<>();

    /**
     * Множество URL-путей, которые дополнительно проверяются на роль администратора.
     */
    private final Set<String> requiredAdminMethods = new HashSet<>();

    {
        ignoreMethods.add("/user/register");
        ignoreMethods.add("/user/login");
        ignoreMethods.add("/v3/api-docs");
        requiredAdminMethods.add("/meter/add");
        requiredAdminMethods.add("/user/user-actions");
        requiredAdminMethods.add("/user/users");
    }

    /**
     * Перехватчик, выполняющий предварительную обработку запроса для проверки авторизации пользователей.
     *
     * @param request  HTTP-запрос.
     * @param response HTTP-ответ.
     * @param handler  Обрабатываемый объект.
     * @return {@code true}, если запрос может быть обработан; в противном случае - {@code false}.
     * @throws Exception Исключение, если произошла ошибка при обработке запроса.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (ignoreMethods.contains(path)
                 || path.toLowerCase(Locale.ROOT).contains("swagger")) {
            return true;
        }
        User currentUser = userService.getUserBySessionId(RequestUtils.getCurrentSessionId(request));
        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        if (requiredAdminMethods.contains(path) && !currentUser.isUserAdministrator()) {
            response.setStatus(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);
            return false;
        }
        return true;
    }

}
