package ru.tipsauk.monitoring.service.in.DatabaseServiceImpl;

import ru.tipsauk.monitoring.model.Meter;
import ru.tipsauk.monitoring.model.MeterValue;
import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.model.UserActionType;
import ru.tipsauk.monitoring.repository.MeterRepository;
import ru.tipsauk.monitoring.repository.MeterValueRepository;
import ru.tipsauk.monitoring.repository.UserActionRepository;
import ru.tipsauk.monitoring.service.in.MeterService;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

/**
 * Реализация интерфейса MeterService с использованием базы данных.
 */
public class DatabaseMeterServiceImpl implements MeterService {

    private final MeterRepository meterRepository;

    private final MeterValueRepository meterValueRepository;

    private final UserActionRepository userActionRepository;

    /**
     * Конструктор класса.
     *
     * @param meterRepository        репозиторий счетчиков.
     * @param meterValueRepository   репозиторий показаний счетчиков.
     * @param userActionRepository   репозиторий действий пользователей.
     */
    public DatabaseMeterServiceImpl(MeterRepository meterRepository
            , MeterValueRepository meterValueRepository, UserActionRepository userActionRepository) {
        this.meterRepository = meterRepository;
        this.meterValueRepository = meterValueRepository;
        this.userActionRepository = userActionRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Meter> getAllMeters() {
        return meterRepository.getAllMeters();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean transmitMeterValue(User user, Meter meter, int value) {
        MeterValue actualValue = meterValueRepository
                .getValueMeterByDateAndUser(LocalDate.now().withDayOfMonth(1), user);
        if (actualValue.getMeterValues().containsKey(meter)) {
            user.addUserAction(UserActionType.ERROR,"За текущий месяц показания по "
                    + meter.getName() + " уже переданы!");
            return false;
        }
        userActionRepository.saveUserAction(user, UserActionType.TRANSMIT_VALUES, meter.getName());
        return meterValueRepository.saveMeterValue(meter, value, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MeterValue getValueMeter(User user, LocalDate dateValue) {
        userActionRepository.saveUserAction(user, UserActionType.GETTING_VALUES, "На дату: " + dateValue);
        MeterValue meterValue = meterValueRepository.getValueMeterByDateAndUser(dateValue, user);
        return meterValue == null ? new MeterValue(LocalDate.now()) : meterValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MeterValue getLastValueMeter(User user) {
        return meterValueRepository.getLastValueMeter(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreeSet<MeterValue> getValueMeterHistory(User user) {
        return meterValueRepository.getAllMeterValuesOrderedByDateValue(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addNewMeter(Meter meter) {
        if (meterRepository.getAllMeters().contains(meter)) {
            System.out.println("Данный счетчик уже добавлен!");
            return;
        }
        meterRepository.saveMeter(meter);
    }
}
