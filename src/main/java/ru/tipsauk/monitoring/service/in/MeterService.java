package ru.tipsauk.monitoring.service.in;


import ru.tipsauk.monitoring.model.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeSet;

/**
 * Класс сервиса для управления показаниями счетчиков.
 */
public class MeterService {

    /**
     * Передает показания счетчика для указанного пользователя.
     *
     * @param user  Пользователь, для которого передаются показания.
     * @param meter Счетчик, для которого передаются показания.
     * @param value Передаваемое значение показаний счетчика.
     * @return true, если передача показаний прошла успешно, иначе false.
     */
    public boolean transmitMeterValue(User user, Meter meter, int value) {
        MeterValue actualValue = getActualValueMeter(user);
        if (actualValue.getMeterValues().containsKey(meter)) {
            user.addUserAction(UserActionType.ERROR,"За текущий месяц показания по "
                    + meter.getName() + " уже переданы!");
            return false;
        }
        actualValue.addMeterValue(meter, value);
        user.addUserAction(UserActionType.TRANSMIT_VALUES, meter.getName());
        return true;
    }

    /**
     * Получает показания счетчика для указанного пользователя на указанную дату.
     *
     * @param user      Пользователь, для которого запрашиваются показания.
     * @param dateValue Дата, на которую запрашиваются показания.
     * @return Показания счетчика на указанную дату.
     */
    public MeterValue getValueMeter(User user, LocalDate dateValue) {
        user.addUserAction(UserActionType.GETTING_VALUES, "На дату: " + dateValue);
        MeterValue meterValue = user.getIndications().get(dateValue);
        return meterValue == null ? new MeterValue() : meterValue;
    }

    /**
     * Получает актуальные показания счетчика для указанного пользователя.
     *
     * @param user Пользователь, для которого запрашиваются актуальные показания.
     * @return Актуальные показания счетчика.
     */
    private MeterValue getActualValueMeter(User user) {
        LocalDate actualDate = LocalDate.now().withDayOfMonth(1);
        MeterValue actualValue = user.getIndications().get(actualDate);
        if (actualValue == null) {
            actualValue = new MeterValue(actualDate);
            user.addValueToUser(actualDate, actualValue);
        }
        return actualValue;
    }

    /**
     * Получает последние (актальные) показания счетчика для указанного пользователя.
     *
     * @param user Пользователь, для которого запрашиваются последние показания.
     * @return Последние показания счетчика.
     */
    public MeterValue getLastValueMeter(User user) {
        Map.Entry<LocalDate, MeterValue> maxEntry = user.getIndications()
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByKey())
                .orElse(null);
        return (maxEntry == null) ? new MeterValue() : maxEntry.getValue();
    }

    /**
     * Получает историю показаний счетчика для указанного пользователя.
     *
     * @param user Пользователь, для которого запрашивается история показаний.
     * @return История показаний счетчика в виде упорядоченного TreeSet.
     */
    public TreeSet<MeterValue> getValueMeterHistory(User user) {
        return new TreeSet<>(user.getIndications().values());
    }

}
