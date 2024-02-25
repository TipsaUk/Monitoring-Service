package ru.tipsauk.monitoring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * {@link AppConfig} - конфигурационный класс для Spring-приложения.
 *
 * <p>Этот класс конфигурации включает перехватчик запросов для контроля авторизации пользователей.
 */

@Configuration
public class AppConfig implements WebMvcConfigurer  {

    /**
     * Контекст приложения Spring.
     */
    private final ApplicationContext applicationContext;

    /**
     * Создает новый экземпляр {@link AppConfig}.
     *
     * @param applicationContext контекст приложения
     */
    @Autowired
    public AppConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Добавляет перехватчик запросов для авторизации пользователей.
     *
     * @param registry реестр перехватчиков
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        AuthorizationInterceptor authorizationInterceptor = applicationContext.getBean(AuthorizationInterceptor.class);
        registry.addInterceptor(authorizationInterceptor).addPathPatterns("/**");
    }

}
