package ru.tipsauk.monitoring.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.tipsauk.monitoring.service.in.UserService;
import ru.tipsauk.user_audit_starter.event.UserAuditEvent;

/**
 * Компонент, обрабатывающий события аудита пользователя.
 */
@Component
@RequiredArgsConstructor
public class UserAuditEventListener {

    /**
     * Сервис пользователя для сохранения действий пользователя.
     */
    private final UserService userService;

    /**
     * Обработчик событий аудита пользователя.
     *
     * @param event Событие аудита пользователя.
     */
    @EventListener
    public void handleUserAuditEvent(UserAuditEvent event) {
        userService.saveUserAction(event.getSessionId(), event.getActionType(), event.getDescription());
    }

}
