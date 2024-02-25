package ru.tipsauk.user_audit_starter.annotations;

import org.springframework.context.annotation.Import;
import ru.tipsauk.user_audit_starter.config.EnableAudit;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
@Import({EnableAudit.class})
public @interface EnableUserAudit {
}
