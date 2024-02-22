package ru.tipsauk.monitoring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение для ответов, когда ресурс не найден.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    /**
     * Сообщение об ошибке.
     */
    private final String message;

    /**
     * Конструктор исключения с указанием сообщения.
     *
     * @param message Сообщение об ошибке.
     */
    public NotFoundException(String message) {
        super(message);
        this.message = message;
    }

}
