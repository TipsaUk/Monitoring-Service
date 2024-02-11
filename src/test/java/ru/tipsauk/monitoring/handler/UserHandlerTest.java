package ru.tipsauk.monitoring.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.tipsauk.monitoring.dto.UserActionDto;
import ru.tipsauk.monitoring.handler.mapper.UserActionMapper;
import ru.tipsauk.monitoring.handler.mapper.UserMapper;
import ru.tipsauk.monitoring.handler.util.RequestUtils;
import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.model.UserRole;
import ru.tipsauk.monitoring.service.in.UserService;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserHandlerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserActionMapper userActionMapper;

    @InjectMocks
    private UserHandler userHandler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Тест на успешную регистрацию")
    void testSignUpSuccess() throws Exception {
        String jsonRequestBody = "{\"nickName\":\"testUser\",\"password\":\"123\",\"role\":\"USER\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequestBody)));
        when(request.getContentType()).thenReturn("application/json");
        when(userService.signUp(any(),any())).thenReturn(true);
        userHandler.signUp(request, response);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(userService).signUp("testUser", "123");
        verify(response).getWriter();
        verify(response.getWriter()).write(anyString());
    }

    @Test
    @DisplayName("Тест на вход пользователя в систему")
    void testSignInSuccess() throws Exception {
        String jsonRequestBody = "{\"nickName\":\"testUser\",\"password\":\"123\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequestBody)));
        when(request.getContentType()).thenReturn("application/json");
        userHandler.signIn(request, response);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).getWriter();
        verify(response.getWriter()).write(anyString());
        verify(request.getSession()).setAttribute("sessionId", anyString());
    }

    @Test
    @DisplayName("Тест на выход пользователя из системы")
    public void testSignOutSuccess() {
        when(RequestUtils.getCurrentSessionId(request)).thenReturn("sessionId");
        when(userService.signOutBySessionId("sessionId")).thenReturn(true);
        userHandler.signOut(request, response);
        verify(userService).signOutBySessionId("sessionId");
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Тест на успешное получение истории действий пользователя")
    public void testGetUserActionsSuccess() throws Exception {
        User user = new User("user", "123", UserRole.USER);
        when(request.getParameter("user")).thenReturn("user");
        when(RequestUtils.getUserForGettingData(userService, request, response)).thenReturn(user);
        when(userActionMapper.userActionToUserActionDto(any())).thenReturn(new UserActionDto());
        userHandler.getUserActions(request, response);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Тест на успешное получение истории действий пользователя")
    public void testGetAllUsersSuccess() throws Exception {
        User user = new User("user", "123", UserRole.USER);
        when(request.getParameter("user")).thenReturn("user");
        when(RequestUtils.getCurrentSessionId(request)).thenReturn("sessionId");
        when(userService.getUserBySessionId("sessionId")).thenReturn(user);
        when(user.isUserAdministrator()).thenReturn(true);
        when(objectMapper.writeValueAsString(any())).thenReturn("JsonString");
        userHandler.getAllUsers(request, response);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(objectMapper).writeValueAsString(any());

    }
}