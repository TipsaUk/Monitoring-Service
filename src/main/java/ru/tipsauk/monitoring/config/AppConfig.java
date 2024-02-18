package ru.tipsauk.monitoring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Properties;

/**
 * {@link AppConfig} - конфигурационный класс для Spring-приложения.
 *
 * <p>Этот класс конфигурации включает веб-модуль MVC и автопрокси AspectJ.
 */
@EnableWebMvc
@Configuration
@ComponentScan("ru.tipsauk.monitoring")
@EnableAspectJAutoProxy
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
     * Настраивает конфигуратор для подстановки значений свойств.
     *
     * @return конфигуратор для подстановки значений свойств
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setProperties(loadProperties());
        return configurer;
    }

    /**
     * Загружает свойства из файла application.yml.
     *
     * @return загруженные свойства
     */
    private static Properties loadProperties() {
        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
        yamlPropertiesFactoryBean.setResources(new ClassPathResource("application.yml"));
        return yamlPropertiesFactoryBean.getObject();
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
