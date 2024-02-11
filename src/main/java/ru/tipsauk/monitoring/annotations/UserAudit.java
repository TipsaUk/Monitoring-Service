package ru.tipsauk.monitoring.annotations;

import ru.tipsauk.monitoring.model.UserActionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UserAudit {
    UserActionType actionType() default UserActionType.ERROR;
}
