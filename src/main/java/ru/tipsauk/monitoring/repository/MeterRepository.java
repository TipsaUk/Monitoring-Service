package ru.tipsauk.monitoring.repository;
import ru.tipsauk.monitoring.model.Meter;

import java.util.Set;

/**
 * Интерфейс для взаимодействия с базой данных по данным счетчиков.
 */
public interface MeterRepository {

    /**
     * Сохраняет новый счетчик в БД.
     *
     * @param meter объект счетчика, который нужно сохранить.
     */
    boolean saveMeter(Meter meter);

    /**
     * Получает все счетчики из БД.
     *
     * @return множество счетчиков.
     */
    Set<Meter> getAllMeters();

    /**
     * Получает счетчик по имени из БД.
     *
     * @return - полученный счетчик.
     */
    Meter getMeterByName(String name);

}
