package ru.tipsauk.monitoring.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.tipsauk.monitoring.annotations.UserAudit;
import ru.tipsauk.monitoring.util.RequestUtils;
import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.repository.UserActionRepository;
import ru.tipsauk.monitoring.service.in.UserService;

/**
 * Аспект для аудита пользовательских действий.
 * Аспект выполняет логирование действий пользователей, помеченных аннотацией UserAudit.
 */
@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class UserAuditAspect {

    private UserActionRepository userActionRepository;
    private UserService userService;

    @Pointcut("execution(@ru.tipsauk.monitoring.annotations.UserAudit * *(..))")
    public void annotatedByUserAudit() {

    }

    @Around("annotatedByUserAudit() && @annotation(userAudit)")
    public Object logging(ProceedingJoinPoint proceedingJoinPoint, UserAudit userAudit) throws Throwable {
        log.info("Аудит пользователя " + proceedingJoinPoint.getSignature());
        User user = extractUser(proceedingJoinPoint);
        String description = extractDescription(proceedingJoinPoint);
        if (user != null) {
            userActionRepository.saveUserAction(user, userAudit.actionType(), description);
        }
        return proceedingJoinPoint.proceed();
    }

    private User extractUser(ProceedingJoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof HttpServletRequest) {
                return userService.getUserBySessionId(RequestUtils.getCurrentSessionId((HttpServletRequest) arg));
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

}
