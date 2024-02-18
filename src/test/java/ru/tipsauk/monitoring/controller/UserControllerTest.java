package ru.tipsauk.monitoring.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.tipsauk.monitoring.dto.UserActionDto;
import ru.tipsauk.monitoring.dto.UserDto;
import ru.tipsauk.monitoring.exception.NotFoundException;
import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.model.UserRole;
import ru.tipsauk.monitoring.service.in.UserService;
import ru.tipsauk.monitoring.util.RequestUtils;

import static org.mockito.Mockito.*;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class UserControllerTest {

    private MockMvc mockMvc;
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    @DisplayName("MockMvc тест на успешную регистрацию")
    void testSignUp() throws Exception {
        UserDto userDto = new UserDto("username", "password");
        Mockito.when(userService.signUp(userDto.getNickName(), userDto.getPassword())).thenReturn(true);
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Тест на успешную регистрацию")
    void signUp_Success() {
        UserDto userDto = new UserDto("username", "password");
        when(userService.signUp(any(), any())).thenReturn(true);
        ResponseEntity<String> response = userController.signUp(userDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Success!");
    }

    @Test
    @DisplayName("Тест на неуспешную регистрацию")
    void signUp_Failure() {
        UserDto userDto = new UserDto("username", "password");
        when(userService.signUp(any(), any())).thenReturn(false);
        ResponseEntity<String> response = userController.signUp(userDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Error");
    }

    @Test
    @DisplayName("MockMvc тест на успешный вход")
    public void testSignIn() throws Exception {
        UserDto userDto = new UserDto("username", "password");
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("sessionId")).thenReturn("sessionId");
        when(userService.signInWithSession(any(), any())).thenReturn("sessionId");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn();
        verify(userService, times(1)).signInWithSession(eq("username"), eq("password"));
        session = result.getRequest().getSession();
        assertThat(session.getAttribute("sessionId")).isEqualTo("sessionId");
        assertThat(result.getResponse().getContentAsString()).isEqualTo("Success!");
    }

    @Test
    @DisplayName("Тест на успешный вход")
    void signIn_Success() {
        UserDto userDto = new UserDto("username", "password");
        when(userService.signInWithSession(any(), any())).thenReturn("sessionId");
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("sessionId")).thenReturn("sessionId");
        ResponseEntity<String> response = userController.signIn(userDto, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Success!");
    }

    @Test
    @DisplayName("Тест на неуспешный вход")
    void signIn_Failure() {
        UserDto userDto = new UserDto("username", "password");
        when(userService.signInWithSession(any(), any())).thenReturn(null);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        ResponseEntity<String> response = userController.signIn(userDto, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Error");
    }

    @Test
    @DisplayName("MockMvc тест на успешный выход")
    void testSignOut() throws Exception {
        String sessionId = "sessionId";
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("sessionId")).thenReturn(sessionId);
        when(RequestUtils.getCurrentSessionId(request)).thenReturn("SessionId");
        when(userService.signOutBySessionId(any())).thenReturn(true);
        mockMvc.perform(post("/user/logout")
                        .header("sessionId", sessionId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Тест на успешный выход пользователя")
    void signOut_Success() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(RequestUtils.getCurrentSessionId(request)).thenReturn("SessionId");
        when(userService.signOutBySessionId("SessionId")).thenReturn(true);
        ResponseEntity<String> response = userController.signOut(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Success!");
    }

    @Test
    @DisplayName("Тест на ошибку при выходе пользователя")
    void signOut_Error() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(RequestUtils.getCurrentSessionId(request)).thenReturn("SessionId");
        when(userService.signOutBySessionId("SessionId")).thenReturn(false);
        ResponseEntity<String> response = userController.signOut(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Error");
    }

    @Test
    @DisplayName("MockMvc тест на успешное получение действий пользователя")
    void testGetUserActions() throws Exception {
        String username = "user";
        User user = new User("user", "pass", UserRole.USER);
        TreeSet<UserActionDto> userActions = new TreeSet<>();
        Mockito.when(userService.getUserByName("user")).thenReturn(user);
        Mockito.when(userService.getUserActions(user, null)).thenReturn(userActions);
        mockMvc.perform(MockMvcRequestBuilders.get("/user/user-actions")
                        .param("username", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Тест на успешное получение действий пользователя")
    void getUserActions_Success() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        User user = new User("username", "pass", UserRole.USER);
        when(userService.getUserByName("username")).thenReturn(user);
        when(userService.getUserActions(user, null)).thenReturn(new TreeSet<>());
        ResponseEntity<TreeSet<UserActionDto>> response = userController
                .getUserActions("username", request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("MockMvc тест на успешное получение всех пользователей")
    void testGetAllUsers() throws Exception {
        Set<UserDto> users = new HashSet<>();
        Mockito.when(userService.getAllUsers()).thenReturn(users);
        mockMvc.perform(MockMvcRequestBuilders.get("/user/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Тест на ошибку при отсутствии пользователя")
    void getUserActions_UserNotFound() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(userService.getUserByName(any())).thenReturn(null);
        assertThrows(NotFoundException.class, () -> {
            userController.getUserActions("user", request);
        });
    }

    @Test
    @DisplayName("Тест на успешное получение всех пользователей")
    void getAllUsers_Success() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Set<UserDto> mockedUserDtoSet = new HashSet<>();
        when(userService.getAllUsers()).thenReturn(mockedUserDtoSet);
        ResponseEntity<Set<UserDto>> response = userController.getAllUsers(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(mockedUserDtoSet);
    }

}