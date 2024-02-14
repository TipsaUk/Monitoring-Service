package ru.tipsauk.monitoring.handler.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.service.in.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Set;

public class RequestUtils {

    public static String extractJsonFromRequest(HttpServletRequest request) throws IOException {
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        }
        return jsonBuilder.toString();
    }

    public static void setResponse(HttpServletResponse response, String jsonResponse) throws IOException {
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    public static boolean isContentTypeJson(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.equals("application/json");
    }

    public static String getCurrentSessionId(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object sessionIdAttribute = session.getAttribute("sessionId");
        return (sessionIdAttribute != null) ? sessionIdAttribute.toString() : "";
    }
    public static User getUserForGettingData(UserService userService, HttpServletRequest request, HttpServletResponse response) {
        User user;
        User currentUser = userService.getUserBySessionId(RequestUtils.getCurrentSessionId(request));
        String userName = request.getParameter("user");
        if (userName != null && !userName.isEmpty()) {
            user = userService.getUserByName(userName);
        } else {
            user = currentUser;
        }
        if (user == null || currentUser == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        if (!currentUser.getNickName().equals(userName)
                && !currentUser.isUserAdministrator()) {
            response.setStatus(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);
            return null;
        }
        return user;
    }

    public static <T> boolean isDtoValid(T dto) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<T> violation : violations) {
                System.out.println(violation.getMessage());
            }
            return false;
        }
        return true;
    }
}
