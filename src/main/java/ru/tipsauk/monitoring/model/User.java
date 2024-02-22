package ru.tipsauk.monitoring.model;

/**
 * Класс, сущности пользователя системы.
 */
public class User {

    /** id пользователя в системе. */
    private long id;

    /** Имя пользователя в системе. */
    private String nickName;

    /** Пароль пользователя. */
    private String password;

    /** Роль пользователя. */
    private UserRole role;


    /**
     * Конструктор для создания объекта пользователя с указанными параметрами.
     *
     * @param nickName Имя пользователя.
     * @param password Пароль пользователя.
     * @param role     Роль пользователя.
     */
    public User(String nickName, String password, UserRole role) {
        this.nickName = nickName;
        this.password = password;
        this.role = role;
    }

    public User(long id, String nickName, String password, UserRole role) {
        this.id = id;
        this.nickName = nickName;
        this.password = password;
        this.role = role;
    }

    public User() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }


    /**
     * Проверяет, является ли пользователь администратором.
     *
     * @return true, если пользователь является администратором, иначе false.
     */
    public boolean isUserAdministrator() {
        return role == UserRole.ADMINISTRATOR;
    }

}
