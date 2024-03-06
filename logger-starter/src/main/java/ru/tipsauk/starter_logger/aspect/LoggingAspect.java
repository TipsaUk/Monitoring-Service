package ru.tipsauk.starter_logger.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * Аспект для логирования методов, помеченных аннотацией Loggable.
 */
@Slf4j
@Aspect
public class LoggingAspect {

    @Around("@within(ru.tipsauk.starter_logger.annotations.Loggable) || @annotation(ru.tipsauk.starter_logger.annotations.Loggable)")
    public Object logMethodExecutionTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info("Выполняется метод " + proceedingJoinPoint.getSignature());
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long endTime = System.currentTimeMillis();
        log.info("Метод " + proceedingJoinPoint.getSignature() +
                " выполнен. Время выполнения: " + (endTime - startTime) + " ms");
        return result;
    }

}
