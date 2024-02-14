package ru.tipsauk.monitoring.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ru.tipsauk.monitoring.annotations.UserAudit;
import ru.tipsauk.monitoring.dto.ApiResponse;
import ru.tipsauk.monitoring.dto.UserDto;
import ru.tipsauk.monitoring.handler.mapper.UserActionMapper;
import ru.tipsauk.monitoring.handler.mapper.UserMapper;
import ru.tipsauk.monitoring.handler.util.RequestUtils;
import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.model.UserActionType;
import ru.tipsauk.monitoring.service.in.UserService;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Класс UserHandler предоставляет методы для обработки запросов, связанных с пользователями,
 * их регистрацией, входом в систему, выходом из системы, получением действий пользователя и получением списка всех пользователей.
 */
public class UserHandler {

    /**
     * Сервис для работы с пользователями.
     */
    private final UserService userService;

    /**
     * Объект для преобразования объектов в JSON и наоборот.
     */
    private final ObjectMapper objectMapper;

    /**
     * Маппер для преобразования объектов User в объекты DTO (Data Transfer Object) и наоборот.
     */
    private final UserMapper userMapper;

    /**
     * Маппер для преобразования объектов UserAction в объекты DTO (Data Transfer Object) и наоборот.
     */
    private final UserActionMapper userActionMapper;

    /**
     * Конструктор класса UserHandler.
     *
     * @param userService       Сервис для работы с пользователями.
     * @param userMapper        Маппер для преобразования объектов User в DTO и наоборот.
     * @param userActionMapper  Маппер для преобразования объектов UserAction в DTO и наоборот.
     */
    public UserHandler(UserService userService, UserMapper userMapper, UserActionMapper userActionMapper) {
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
        this.userMapper = userMapper;
        this.userActionMapper = userActionMapper;
    }

    /**
     * Метод для регистрации нового пользователя.
     *
     * @param request  Запрос от клиента.
     * @param response Ответ сервера.
     */
    public void signUp(HttpServletRequest request, HttpServletResponse response) {
        ApiResponse apiResponse;
        try {
            UserDto userDto = getUserDtoFromRequest(request, response);
            if (userDto == null) {
                return;
            }
            apiResponse = userService.signUp(userDto.getNickName(), userDto.getPassword())
                    ? new ApiResponse("success", "Регистрация успешна!")
                    : new ApiResponse("error", "Внутренняя ошибка при добавлении пользователя");
            RequestUtils.setResponse(response, objectMapper.writeValueAsString(apiResponse));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Метод для входа пользователя в систему.
     *
     * @param request  Запрос от клиента.
     * @param response Ответ сервера.
     */
    @UserAudit(actionType = UserActionType.SIGN_IN)
    public void signIn(HttpServletRequest request, HttpServletResponse response) {
        ApiResponse apiResponse;
        try {
            UserDto userDto = getUserDtoFromRequest(request, response);
            if (userDto == null) {
                return;
            }
            String sessionId = userService.signInWithSession(userDto.getNickName(), userDto.getPassword());
            if (sessionId != null) {
                apiResponse = new ApiResponse("success", "Вход успешен!");
                HttpSession session = request.getSession();
                session.setAttribute("sessionId", sessionId);
            } else {
                apiResponse = new ApiResponse("error", "Ошибка при входе");
            }
            RequestUtils.setResponse(response, objectMapper.writeValueAsString(apiResponse));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Метод для выхода пользователя из системы.
     *
     * @param request  Запрос от клиента.
     * @param response Ответ сервера.
     */
    @UserAudit(actionType = UserActionType.SIGN_OUT)
    public void signOut(HttpServletRequest request, HttpServletResponse response) {
        try {
            String sessionId = RequestUtils.getCurrentSessionId(request);
            ApiResponse apiResponse = userService.signOutBySessionId(sessionId)
                    ? new ApiResponse("success", "Выход успешно выполнен!")
                    : new ApiResponse("error", "Ошибка при выходе пользователя");
            RequestUtils.setResponse(response, objectMapper.writeValueAsString(apiResponse));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Метод для получения действий пользователя.
     *
     * @param request  Запрос от клиента.
     * @param response Ответ сервера.
     */
    @UserAudit(actionType = UserActionType.GETTING_VALUES)
    public void getUserActions(HttpServletRequest request, HttpServletResponse response) {
        User user = RequestUtils.getUserForGettingData(userService, request, response);
        if (user == null) {
            return;
        }
        try {
        RequestUtils.setResponse(response,
                objectMapper.writeValueAsString(userService.getUserActions(user, null).stream()
                        .map(userActionMapper::userActionToUserActionDto)
                        .collect(Collectors.toSet())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Метод для получения списка всех пользователей.
     *
     * @param request  Запрос от клиента.
     * @param response Ответ сервера.
     */
    @UserAudit(actionType = UserActionType.GETTING_VALUES)
    public void getAllUsers(HttpServletRequest request, HttpServletResponse response) {
        User currentUser = userService.getUserBySessionId(RequestUtils.getCurrentSessionId(request));
        if (!currentUser.isUserAdministrator()) {
            response.setStatus(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);
            return;
        }
        try {
            RequestUtils.setResponse(response,
                    objectMapper.writeValueAsString(userService.getAllUsers().stream()
                            .map(userMapper::userToUserDto)
                            .collect(Collectors.toSet())));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Вспомогательный метод для получения объекта UserDto из запроса.
     *
     * @param request  Запрос от клиента.
     * @param response Ответ сервера.
     * @return Объект UserDto или null, если произошла ошибка.
     * @throws IOException Исключение, возникающее при работе с вводом/выводом.
     */
    private UserDto getUserDtoFromRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!RequestUtils.isContentTypeJson(request)) {
            response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return null;
        }
        String jsonString = RequestUtils.extractJsonFromRequest(request);
        UserDto userDto = objectMapper.readValue(jsonString, UserDto.class);
        if (!RequestUtils.isDtoValid(userDto)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        return userDto;
    }

}
