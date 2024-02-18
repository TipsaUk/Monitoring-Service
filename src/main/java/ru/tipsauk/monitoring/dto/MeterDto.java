package ru.tipsauk.monitoring.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Класс, представляющий объект данных счетчика.
 *
 */
public class MeterDto {

    /**
     * Имя счетчика.
     */
    @Schema(description = "Имя счетчика")
    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    /**
     * Конструктор без параметров для создания пустого объекта MeterDto.
     */
    public MeterDto() {
    }

    /**
     * Конструктор для создания объекта MeterDto с указанным именем.
     *
     * @param name Имя счетчика.
     */
    public MeterDto(String name) {
        this.name = name;
    }

    /**
     * Получить текущее имя счетчика.
     *
     * @return Имя счетчика.
     */
    public String getName() {
        return name;
    }

    /**
     * Установить новое имя счетчика.
     *
     * @param name Новое имя счетчика.
     */
    public void setName(String name) {
        this.name = name;
    }
}
