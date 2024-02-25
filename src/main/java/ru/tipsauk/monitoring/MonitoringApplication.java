package ru.tipsauk.monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import ru.tipsauk.user_audit_starter.annotations.EnableUserAudit;

/**
 * Основной класс приложения для передачи - контроля показаний счетчиков.
 */
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableUserAudit
public class MonitoringApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitoringApplication.class, args);
    }

}
