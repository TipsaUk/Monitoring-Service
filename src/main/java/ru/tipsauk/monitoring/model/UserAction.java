package ru.tipsauk.monitoring.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Класс, сущности для хранения действия пользователя в системе.
 */
@Getter
@Setter
@AllArgsConstructor
public class UserAction implements Comparable<UserAction> {

    /** Время действия. */
    private LocalDateTime timeAction;

    /** Действие пользователя. */
    private UserActionType action;

    /** Дополнительное описание действия. */
    private String description;

    @Override
    public int compareTo(UserAction o) {
        return this.timeAction.compareTo(o.getTimeAction());
    }
}
