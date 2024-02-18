package ru.tipsauk.monitoring;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Инициализатор веб-приложения.
 */
public class MainWebAppInitializer implements WebApplicationInitializer {

    /**
     * Настройка веб-приложения при запуске.
     *
     * @param container Контекст сервлет-контейнера.
     */
    @Override
    public void onStartup(ServletContext container) throws ServletException {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.scan("ru.tipsauk.monitoring");
        container.addListener(new ContextLoaderListener(context));
        ServletRegistration.Dynamic dispatcher = container.addServlet("mvc", new DispatcherServlet(context));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }
}
