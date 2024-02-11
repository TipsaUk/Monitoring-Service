package ru.tipsauk.monitoring.dto;

import jakarta.validation.constraints.NotBlank;

import ru.tipsauk.monitoring.model.UserRole;

/**
 * Класс, представляющий объект данных о пользователе.
 *
 */
public class UserDto {

    /** id пользователя в системе. */
    private long id;

    /** Имя пользователя в системе. */
    @NotBlank(message = "Имя не может быть пустым")
    private String nickName;

    /** Пароль пользователя. */
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;

    /** Роль пользователя. */
    private UserRole role;

    /**
     * Конструктор без параметров для создания пустого объекта UserDto.
     */
    public UserDto() {
    }

    /**
     * Получить идентификатор пользователя.
     *
     * @return Идентификатор пользователя.
     */
    public long getId() {
        return id;
    }

    /**
     * Установить новый идентификатор пользователя.
     *
     * @param id Новый идентификатор пользователя.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Получить имя пользователя.
     *
     * @return Имя пользователя.
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * Установить новое имя пользователя.
     *
     * @param nickName Новое имя пользователя.
     */
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     * Получить пароль пользователя.
     *
     * @return Пароль пользователя.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Установить новый пароль пользователя.
     *
     * @param password Новый пароль пользователя.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Получить роль пользователя.
     *
     * @return Роль пользователя.
     */
    public UserRole getRole() {
        return role;
    }

    /**
     * Установить новую роль пользователя.
     *
     * @param role Новая роль пользователя.
     */
    public void setRole(UserRole role) {
        this.role = role;
    }
}
