package ru.tipsauk.monitoring.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс, сущности пользователя системы.
 */
@Getter
@Setter
public class User {

    /** Имя пользователя в системе. */
    private String nickName;

    /** Пароль пользователя. */
    private String password;

    /** Роль пользователя. */
    private UserRole role;

    /** Показания счетчиков пользователя. */
    private Map<LocalDate, MeterValue> indications = new HashMap<>();

    /** Действия пользователя в системе. */
    private Map<LocalDateTime, UserAction> userActions = new HashMap<>();

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

    /**
     * Добавляет показания счетчика для пользователя на указанную дату.
     *
     * @param datValue   Дата, на которую добавляются показания (начало месяца).
     * @param meterValue Показания счетчика.
     */
    public void addValueToUser(LocalDate datValue, MeterValue meterValue) {
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

}
