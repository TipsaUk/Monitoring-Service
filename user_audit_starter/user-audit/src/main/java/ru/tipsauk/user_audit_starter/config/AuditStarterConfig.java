package ru.tipsauk.user_audit_starter.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * {@link AuditStarterConfig} - конфигурационный класс для Spring-приложения.
 *
 * <p>Этот класс конфигурации включает аудит пользователя.
 */
@EnableAspectJAutoProxy
@AutoConfiguration
public class AuditStarterConfig {

}

