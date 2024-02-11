package ru.tipsauk.monitoring.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Аспект для логирования методов, помеченных аннотацией Loggable.
 */
@Aspect
public class LoggingAspect {

    @Pointcut("within(@ru.tipsauk.monitoring.annotations.Loggable *) && execution(* *(..))")
    public void annotatedByLoggable() {

    }

    @Around("annotatedByLoggable()")
    public Object logging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("Выполняется метод " + proceedingJoinPoint.getSignature());
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long endTime = System.currentTimeMillis();
        System.out.println("Метод " + proceedingJoinPoint.getSignature() +
                " выполнен. Время выполнения: " + (endTime - startTime) + " ms");
        return result;
    }
}
