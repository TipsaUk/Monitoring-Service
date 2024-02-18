package ru.tipsauk.monitoring.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Класс, сущности для хранения показаний счетчиков.
 */
public class MeterValue implements Comparable<MeterValue> {

    /** Дата (месяц) показаний. */
    private final LocalDate dateValue;

    /** Наименование счетчика. */
    private final Map<Meter, Integer> meterValues = new HashMap<>();

    /**
     * Конструктор для создания объекта пользователя с указанными параметрами.
     *
     * @param dateValue дата (месяц) показаний.
     */
    public MeterValue(LocalDate dateValue) {
        this.dateValue = dateValue.withDayOfMonth(1);
    }

    public LocalDate getDateValue() {
        return dateValue;
    }

    public Map<Meter, Integer> getMeterValues() {
        return meterValues;
    }

    /**
     * Добавляет показания для счетчика .
     *
     * @param meter Вид счетчика.
     * @param value Показания счетчика.
     */
    public void addMeterReading(Meter meter, Integer value) {
        meterValues.put(meter, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeterValue that = (MeterValue) o;
        return Objects.equals(dateValue, that.dateValue) && Objects.equals(meterValues, that.meterValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateValue, meterValues);
    }

    @Override
    public int compareTo(MeterValue o) {
        return o.dateValue.compareTo(this.dateValue);
    }
}
