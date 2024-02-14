package ru.tipsauk.monitoring.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import ru.tipsauk.monitoring.annotations.UserAudit;
import ru.tipsauk.monitoring.handler.util.RequestUtils;
import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.repository.UserActionRepository;
import ru.tipsauk.monitoring.service.in.UserService;

/**
 * Аспект для аудита пользовательских действий.
 * Аспект выполняет логирование действий пользователей, помеченных аннотацией UserAudit.
 */
@Aspect
public class UserAuditAspect {

    private static UserActionRepository userActionRepository;
    private static UserService userService;

    public static void setUserActionRepository(UserActionRepository userActionRepository) {
        UserAuditAspect.userActionRepository = userActionRepository;
    }

    public static void setUserService(UserService userService) {
        UserAuditAspect.userService = userService;
    }

    @Pointcut("execution(@ru.tipsauk.monitoring.annotations.UserAudit * *(..))")
    public void annotatedByUserAudit() {

    }

    @Around("annotatedByUserAudit() && @annotation(userAudit)")
    public Object logging(ProceedingJoinPoint proceedingJoinPoint, UserAudit userAudit) throws Throwable {
       System.out.println("Аудит пользователя " + proceedingJoinPoint.getSignature());
        User user = extractUser(proceedingJoinPoint);
        if (user != null) {
            userActionRepository.saveUserAction(user, userAudit.actionType(), "");
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

}
