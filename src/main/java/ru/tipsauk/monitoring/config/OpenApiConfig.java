package ru.tipsauk.monitoring.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

/**
 * {@link OpenApiConfig} - конфигурационный класс для swagger.
 *
 * <p>Этот класс конфигурации для описания проекта в swagger-документации.
 */
@OpenAPIDefinition(
        info = @Info(
                title = "API приема показаний счетчиков",
                description = "Сервис для приема показаний счетчиков", version = "1.0"
                )
        )
public class OpenApiConfig {
}
