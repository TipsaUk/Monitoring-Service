package ru.tipsauk.monitoring.dto;

/**
 * Класс, представляющий объект ответа API.
 *
 * <p>Объект ApiResponse содержит информацию о статусе операции и соответствующее сообщение.</p>
 *
 * <p>Статус может принимать значения "success" или "error", а сообщение содержит описание
 * результата операции.</p>
 */
public class ApiResponse {

    /**
     * Статус операции. Может принимать значения "success" или "error".
     */
    private String status;

    /**
     * Сообщение, описывающее результат операции.
     */
    private String message;

    /**
     * Конструктор для создания объекта ApiResponse с указанным статусом и сообщением.
     *
     * @param status Статус операции ("success" или "error").
     * @param message Сообщение, описывающее результат операции.
     */
    public ApiResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
