package ru.tipsauk.monitoring.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Аспект для логирования методов, помеченных аннотацией Loggable.
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("within(@ru.tipsauk.monitoring.annotations.Loggable *) && execution(* *(..))")
    public void annotatedByLoggable() {

    }

    @Around("annotatedByLoggable()")
    public Object logging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info("Выполняется метод " + proceedingJoinPoint.getSignature());
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long endTime = System.currentTimeMillis();
        log.info("Метод " + proceedingJoinPoint.getSignature() +
                " выполнен. Время выполнения: " + (endTime - startTime) + " ms");
        return result;
    }
}
