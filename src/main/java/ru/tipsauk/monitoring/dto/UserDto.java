package ru.tipsauk.monitoring.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.tipsauk.monitoring.model.UserRole;

/**
 * Класс, представляющий объект данных о пользователе.
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class UserDto {

    /** id пользователя в системе. */
    @Schema(description = "id пользователя в системе")
    private long id;

    /** Имя пользователя в системе. */
    @Schema(description = "Имя пользователя в системе")
    @NotBlank(message = "Имя не может быть пустым")
    private String nickName;

    /** Пароль пользователя. */
    @Schema(description = "Пароль пользователя")
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;

    /** Роль пользователя. */
    @Schema(description = "Роль пользователя")
    private UserRole role;

    /**
     * Конструктор для создания объекта пользователя с указанными параметрами.
     *
     * @param nickName Имя пользователя.
     * @param password Пароль пользователя.
     */
    public UserDto(String nickName, String password) {
        this.nickName = nickName;
        this.password = password;
    }

}
