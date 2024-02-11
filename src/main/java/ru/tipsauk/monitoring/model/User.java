package ru.tipsauk.monitoring.model;

//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс, сущности пользователя системы.
 */
//@Getter
//@Setter
//@NoArgsConstructor
public class User {

    /** id пользователя в системе. */
    private long id;

    /** Имя пользователя в системе. */
    private String nickName;

    /** Пароль пользователя. */
    private String password;

    /** Роль пользователя. */
    private UserRole role;

    /** Показания счетчиков пользователя. */
    private final Map<LocalDate, MeterValue> indications = new HashMap<>();

    /** Действия пользователя в системе. */
    private final Map<LocalDateTime, UserAction> userActions = new HashMap<>();

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
        addUserAction(UserActionType.SIGN_UP, "");
    }

    public User(long id, String nickName, String password, UserRole role) {
        this.id = id;
        this.nickName = nickName;
        this.password = password;
        this.role = role;
        addUserAction(UserActionType.SIGN_UP, "");
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

    public Map<LocalDate, MeterValue> getIndications() {
        return indications;
    }

    public Map<LocalDateTime, UserAction> getUserActions() {
        return userActions;
    }

    /**
     * Добавляет показания счетчика для пользователя на указанную дату.
     *
     * @param datValue   Дата, на которую добавляются показания (начало месяца).
     * @param meterValue Показания счетчика.
     */
    public void addMeterValueToUser(LocalDate datValue, MeterValue meterValue) {
       indications.put(datValue.withDayOfMonth(1), meterValue);
    }

    /**
     * Добавляет действие пользователя.
     *
     * @param action      Тип действия пользователя.
     * @param description Описание действия.
     */
    public void addUserAction(UserActionType action, String description) {
        userActions.put(LocalDateTime.now(),
                new UserAction(LocalDateTime.now(), action, description));
        if (action == UserActionType.ERROR) {
            System.out.println(description);
        }
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
