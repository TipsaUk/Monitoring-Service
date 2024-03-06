package ru.tipsauk.starter_logger.config;

import ru.tipsauk.starter_logger.aspect.LoggingAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * {@link LoggerStarterConfig} - конфигурационный класс для Spring-приложения.
 *
 * <p>Этот класс конфигурации включает логирование.
 */
@EnableAspectJAutoProxy
@AutoConfiguration
public class LoggerStarterConfig {

    @Bean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }

}
