package ru.tipsauk.monitoring.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.webmvc.core.configuration.SpringDocWebMvcConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Конфигурационный класс для настройки Swagger документации.
 */
@Configuration
@ComponentScan(basePackages = {"org.springdoc"})
@Import({SpringDocConfiguration.class,
        SpringDocWebMvcConfiguration.class,
        org.springdoc.webmvc.ui.SwaggerConfig.class,
        SwaggerUiConfigProperties.class,
        SwaggerUiOAuthProperties.class,
        JacksonAutoConfiguration.class})
public class AppOpenApiConfig {

    /**
     * Bean - для создания экземпляра OpenAPI (Swagger).
     *
     * @return Экземпляр OpenAPI с настройками информации о документации.
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("API приема показаний счетчиков")
                .version("1.0")
                .description("Сервис для приема показаний счетчиков"));

    }

}
