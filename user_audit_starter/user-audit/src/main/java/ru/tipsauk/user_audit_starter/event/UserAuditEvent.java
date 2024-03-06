package ru.tipsauk.user_audit_starter.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * Класс,для передачи действия пользователя через события.
 */
@Getter
@Setter
public class UserAuditEvent extends ApplicationEvent {

    /** id сессии пользователя. */
    private final String sessionId;

    /** Действие пользователя. */
    private final String actionType;

    /** Дополнительное описание действия. */
    private final String description;

    public UserAuditEvent(Object source, String sessionId, String actionType, String description) {
        super(source);
        this.sessionId = sessionId;
        this.actionType = actionType;
        this.description = description;
    }


}
