package ru.tipsauk.monitoring.service.in.CollectionServicImpl;

import ru.tipsauk.monitoring.model.*;
import ru.tipsauk.monitoring.service.in.MeterService;

import java.time.LocalDate;
import java.util.*;

/**
 * Реализация интерфейса MeterService с использованием коллекций.
 */
public class CollectionMeterServiceImpl implements MeterService {

    private final Set<Meter> meters = new HashSet<>();

    {
        meters.add(new Meter("Счетчик горячей воды"));
        meters.add(new Meter("Счетчик холодной воды"));
        meters.add(new Meter("Счетчик отопления"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Meter> getAllMeters() {
        return meters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean transmitMeterValue(User user, Meter meter, int value) {
        MeterValue actualValue = getActualValueMeter(user);
        if (actualValue.getMeterValues().containsKey(meter)) {
            user.addUserAction(UserActionType.ERROR,"За текущий месяц показания по "
                    + meter.getName() + " уже переданы!");
            return false;
        }
        actualValue.addMeterReading(meter, value);
        user.addUserAction(UserActionType.TRANSMIT_VALUES, meter.getName());
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MeterValue getValueMeter(User user, LocalDate dateValue) {
        user.addUserAction(UserActionType.GETTING_VALUES, "На дату: " + dateValue);
        MeterValue meterValue = user.getIndications().get(dateValue);
        return meterValue == null ? new MeterValue(LocalDate.now()) : meterValue;
    }

    private MeterValue getActualValueMeter(User user) {
        LocalDate actualDate = LocalDate.now().withDayOfMonth(1);
        MeterValue actualValue = user.getIndications().get(actualDate);
        if (actualValue == null) {
            actualValue = new MeterValue(actualDate);
            user.addMeterValueToUser(actualDate, actualValue);
        }
        return actualValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MeterValue getLastValueMeter(User user) {
        Map.Entry<LocalDate, MeterValue> maxEntry = user.getIndications()
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByKey())
                .orElse(null);
        return (maxEntry == null) ? new MeterValue(LocalDate.now()) : maxEntry.getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreeSet<MeterValue> getValueMeterHistory(User user) {
        return new TreeSet<>(user.getIndications().values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addNewMeter(Meter meter) {
        if (meters.contains(meter)) {
            System.out.println("Данный счетчик уже добавлен!");
            return;
        }
        meters.add(meter);
    }
}
