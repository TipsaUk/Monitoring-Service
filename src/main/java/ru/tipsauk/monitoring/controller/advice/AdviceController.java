package ru.tipsauk.monitoring.controller.advice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.tipsauk.monitoring.exception.NotFoundException;

/**
 * Контроллер для обработки ошибок.
 */
@Slf4j
@ControllerAdvice
public class AdviceController {

    @Operation(
            summary = "Обработка ошибки NotFound",
            description = "API для обработки ошибки NotFound",
            responses = {
                    @ApiResponse(responseCode = "404", description = "Ресурс не найден")
            }
    )
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
