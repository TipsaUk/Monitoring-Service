package ru.tipsauk.monitoring.service.in;

import ru.tipsauk.monitoring.dto.MeterDto;
import ru.tipsauk.monitoring.dto.MeterValueDto;
import ru.tipsauk.monitoring.model.Meter;
import ru.tipsauk.monitoring.model.User;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

/**
 * Интерфейс сервиса для управления счетчиками и их показаниями.
 */
public interface MeterService {

    /**
     * Получает все доступные счетчики.
     *
     * @return множество объектов Meter, представляющих все доступные счетчики.
     */
    Set<MeterDto> getAllMeters();

    /**
     * Передает показания счетчика для конкретного пользователя и счетчика.
     *
     * @param user  пользователь, для которого передаются показания счетчика.
     * @param meter счетчик, для которого передаются показания.
     * @param value значение показания счетчика.
     * @return true, если передача была успешной, в противном случае - false.
     */
    boolean transmitMeterValue(User user, Meter meter, int value);

    /**
     * Передает показания счетчика для конкретного пользователя и счетчика (для вэб версии).
     *
     * @param user  пользователь, для которого передаются показания счетчика.
     * @param meterValueDto значения показаний счетчика.
     * @return true, если передача была успешной, в противном случае - false.
     */
    boolean transmitMeterValueWeb(User user, MeterValueDto meterValueDto);

    /**
     * Получает показания счетчика для конкретного пользователя и даты.
     *
     * @param user      пользователь, для которого получаются показания счетчика.
     * @param dateValue дата, для которой получаются показания.
     * @return объект MeterValue, представляющий показания счетчика для указанного пользователя и даты.
     */
    MeterValueDto getValueMeter(User user, LocalDate dateValue);

    /**
     * Получает последние показания счетчика для конкретного пользователя.
     *
     * @param user пользователь, для которого получаются последние показания счетчика.
     * @return объект MeterValue, представляющий последние показания счетчика для указанного пользователя.
     */
    MeterValueDto getLastValueMeter(User user);

    /**
     * Получает историю показаний счетчика для конкретного пользователя.
     *
     * @param user пользователь, для которого получается история показаний счетчика.
     * @return TreeSet объектов MeterValue, представляющих историю показаний счетчика для указанного пользователя.
     */
    TreeSet<MeterValueDto> getValueMeterHistory(User user);

    /**
     * Добавляет новый счетчик.
     *
     * @param nameMeter имя нового счетчика для добавления.
     */
    boolean addNewMeter(String nameMeter);

}
