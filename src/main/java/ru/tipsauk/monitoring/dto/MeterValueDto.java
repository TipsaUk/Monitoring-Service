package ru.tipsauk.monitoring.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import ru.tipsauk.monitoring.model.Meter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс, представляющий объект данных о показаниях счетчика.
 *
 */
public class MeterValueDto {

    /**
     * Дата показаний.
     */
    @NotNull(message = "Дата не может быть пустой")
    private Date dateValue;

    /**
     * Показания счетчиков.
     */
    @NotEmpty(message = "Показания не могут быть пустыми")
    @NotNull(message = "Показания не могут быть пустыми")
    private final Map<Meter, Integer> meterValues = new HashMap<>();

    /**
     * Конструктор без параметров для создания пустого объекта MeterValueDto.
     */
    public MeterValueDto() {
    }

    /**
     * Получить текущую дату показаний.
     *
     * @return Дата показаний.
     */
    public Date getDateValue() {
        return dateValue;
    }

    /**
     * Установить новую дату показаний.
     *
     * @param dateValue Новая дата показаний.
     */
    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    /**
     * Получить показания счетчиков.
     *
     * @return Показания счетчиков.
     */
    public Map<Meter, Integer> getMeterValues() {
        return meterValues;
    }

}
