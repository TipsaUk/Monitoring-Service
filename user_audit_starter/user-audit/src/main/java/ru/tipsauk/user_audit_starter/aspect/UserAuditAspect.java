package ru.tipsauk.user_audit_starter.aspect;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import ru.tipsauk.user_audit_starter.annotations.UserAudit;
import ru.tipsauk.user_audit_starter.event.UserAuditEvent;

/**
 * Аспект для аудита пользовательских действий.
 * Аспект выполняет логирование действий пользователей, помеченных аннотацией UserAudit.
 */
@Slf4j
@Aspect
@Component
public class UserAuditAspect {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Pointcut("execution(@ru.tipsauk.user_audit_starter.annotations.UserAudit * *(..))")
    public void annotatedByUserAudit() {

    }

    @Around("annotatedByUserAudit() && @annotation(userAudit)")
    public Object logging(ProceedingJoinPoint proceedingJoinPoint, UserAudit userAudit) throws Throwable {
        log.info("Аудит пользователя " + proceedingJoinPoint.getSignature());
        String sessionId = extractSessionId(proceedingJoinPoint);
        String description = extractDescription(proceedingJoinPoint);
        log.info("Аудит пользователя sessionId - " + sessionId);
        if (sessionId != null && !sessionId.isEmpty()) {
            eventPublisher.publishEvent(new UserAuditEvent(this, sessionId, userAudit.actionType(), description));
        }
        return proceedingJoinPoint.proceed();
    }

    private String extractSessionId(ProceedingJoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof HttpServletRequest) {
                return getCurrentSessionId((HttpServletRequest) arg);
            }
        }
        return null;
    }

    private String extractDescription(ProceedingJoinPoint joinPoint) {
        StringBuilder builder = new StringBuilder();
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof String) {
                builder.append((builder.length() > 0 ? "; " : "")).append(arg);
            }
        }
        return builder.toString();
    }

    public static String getCurrentSessionId(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object sessionIdAttribute = session.getAttribute("sessionId");
        return (sessionIdAttribute != null) ? sessionIdAttribute.toString() : "";
    }
}
