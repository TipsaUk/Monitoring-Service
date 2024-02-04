package ru.tipsauk.monitoring.repository;

import ru.tipsauk.monitoring.model.Meter;
import ru.tipsauk.monitoring.model.MeterValue;
import ru.tipsauk.monitoring.model.User;

import java.time.LocalDate;
import java.util.TreeSet;

/**
 * Интерфейс для взаимодействия с базой данных для показаний счетчиков.
 */
public interface MeterValueRepository {

    /**
     * Получает последние показания счетчика для указанного пользователя.
     *
     * @param user пользователь, для которого нужно получить последние показания.
     * @return объект MeterValue с последними показаниями счетчика.
     */
    MeterValue getLastValueMeter(User user);

    /**
     * Получает показания счетчика на указанную дату для указанного пользователя.
     *
     * @param dateValue дата, на которую нужно получить показания.
     * @param user      пользователь, для которого нужно получить показания.
     * @return объект MeterValue с показаниями счетчика на указанную дату.
     */
    MeterValue getValueMeterByDateAndUser(LocalDate dateValue, User user);

    /**
     * Сохраняет показания счетчика в БД.
     *
     * @param meter счетчик, для которого сохраняются показания.
     * @param value значение показаний.
     * @param user пользователь, для которого сохраняются показания.
     * @return true, если сохранение успешно, иначе false.
     */
    boolean saveMeterValue(Meter meter, int value, User user);

    /**
     * Получает все показания счетчиков для указанного пользователя, упорядоченные по дате.
     *
     * @param user пользователь, для которого нужно получить все показания.
     * @return упорядоченное множество MeterValue с показаниями счетчиков.
     */
    TreeSet<MeterValue> getAllMeterValuesOrderedByDateValue(User user);
}
