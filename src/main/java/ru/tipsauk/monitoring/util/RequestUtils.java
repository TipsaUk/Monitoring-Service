package ru.tipsauk.monitoring.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Класс для работы с HTTP-запросами.
 */
public class RequestUtils {

    /**
     * Получение текущего идентификатора сеанса пользователя из запроса.
     *
     * @param request HTTP-запрос.
     * @return Идентификатор сеанса пользователя.
     */
    public static String getCurrentSessionId(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object sessionIdAttribute = session.getAttribute("sessionId");
        return (sessionIdAttribute != null) ? sessionIdAttribute.toString() : "";
    }

}
