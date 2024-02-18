package ru.tipsauk.monitoring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tipsauk.monitoring.annotations.UserAudit;
import ru.tipsauk.monitoring.dto.UserActionDto;
import ru.tipsauk.monitoring.dto.UserDto;
import ru.tipsauk.monitoring.exception.NotFoundException;
import ru.tipsauk.monitoring.util.RequestUtils;
import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.model.UserActionType;
import ru.tipsauk.monitoring.service.in.UserService;

import java.util.Set;
import java.util.TreeSet;

@Tag(
        name = "API для работы с пользователями",
        description = "API для управления пользователями (регисртация, вход, выход и др.)"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {

    /**
     * Сервис для работы с пользователями.
     */
    private final UserService userService;

    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Регистрация нового пользователя с никнеймом и паролем."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Регистрация пользователя успешна"),
            @ApiResponse(responseCode = "400", description = "Ошибка во время регистрации пользователя")
    })
    @PostMapping("/register")
    public ResponseEntity<String> signUp(@Valid @RequestBody UserDto userDto) {
        return userService.signUp(userDto.getNickName(), userDto.getPassword())
                ? ResponseEntity.ok("Success!")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error");
    }


    @Operation(
            summary = "Вход пользователя",
            description = "Вход пользователя с никнеймом и паролем."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Вход пользователя успешен"),
            @ApiResponse(responseCode = "400", description = "Ошибка во время входа пользователя")
    })
    @PostMapping("/login")
    @UserAudit(actionType = UserActionType.SIGN_IN)
    public ResponseEntity<String> signIn(@Valid @RequestBody UserDto userDto, HttpServletRequest request) {
        String sessionId = userService.signInWithSession(userDto.getNickName(), userDto.getPassword());
        if (sessionId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error");
        }
        HttpSession session = request.getSession();
        session.setAttribute("sessionId", sessionId);
        return ResponseEntity.ok("Success!");
    }


    @Operation(
            summary = "Выход пользователя",
            description = "Выход текущего аутентифицированного пользователя."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Выход пользователя успешен"),
            @ApiResponse(responseCode = "400", description = "Ошибка во время выхода пользователя")
    })
    @PostMapping("/logout")
    @UserAudit(actionType = UserActionType.SIGN_OUT)
    public ResponseEntity<String> signOut(HttpServletRequest request) {
        String sessionId = RequestUtils.getCurrentSessionId(request);
        return userService.signOutBySessionId(sessionId)
                ? ResponseEntity.ok("Success!")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error");
    }


    @Operation(
            summary = "Получение действий пользователя",
            description = "Получение действий, выполненных определенным пользователем."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Действия пользователя успешно получены",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))
                    }),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/user-actions")
    @UserAudit(actionType = UserActionType.GETTING_SYSTEM_INFO)
    public ResponseEntity<TreeSet<UserActionDto>> getUserActions(
            @Parameter(description = "Имя пользователя") String username, HttpServletRequest request) {
        User user = userService.getUserByName(username);
        if (user == null) {
            throw new NotFoundException("User not found: " + username);
        }
        return ResponseEntity.ok(userService.getUserActions(user, null));
    }

    @Operation(
            summary = "Получение всех пользователей",
            description = "Получение списка всех пользователей (только для администратора)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователи успешно получены",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))
                    })
    })
    @GetMapping("/users")
    @UserAudit(actionType = UserActionType.GETTING_SYSTEM_INFO)
    public ResponseEntity<Set<UserDto>> getAllUsers(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

}
