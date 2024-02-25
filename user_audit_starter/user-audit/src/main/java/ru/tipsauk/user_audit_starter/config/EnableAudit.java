package ru.tipsauk.user_audit_starter.config;

import org.springframework.context.annotation.Bean;
import ru.tipsauk.user_audit_starter.aspect.UserAuditAspect;

/**
 * {@link AuditStarterConfig} - конфигурационный класс для Spring-приложения.
 *
 * <p>Этот класс конфигурации включает аудит пользователя аннотацией @EnableUserAudit.
 */
public class EnableAudit {

    @Bean
    public UserAuditAspect userAuditAspect() {
        return new UserAuditAspect();
    }
}
