package ru.tipsauk.monitoring.model;

//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Класс, сущности для хранения действия пользователя в системе.
 */
//@Getter
//@Setter
//@AllArgsConstructor
public class UserAction implements Comparable<UserAction> {

    /** Время действия. */
    private LocalDateTime timeAction;

    /** Действие пользователя. */
    private UserActionType action;

    /** Дополнительное описание действия. */
    private String description;

    public UserAction(LocalDateTime timeAction, UserActionType action, String description) {
        this.timeAction = timeAction;
        this.action = action;
        this.description = description;
    }

    public LocalDateTime getTimeAction() {
        return timeAction;
    }

    public void setTimeAction(LocalDateTime timeAction) {
        this.timeAction = timeAction;
    }

    public UserActionType getAction() {
        return action;
    }

    public void setAction(UserActionType action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int compareTo(UserAction o) {
        return this.timeAction.compareTo(o.getTimeAction());
    }
}
