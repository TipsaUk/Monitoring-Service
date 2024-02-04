package ru.tipsauk.monitoring.in;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.tipsauk.monitoring.config.ApplicationConfig;
import ru.tipsauk.monitoring.model.*;
import ru.tipsauk.monitoring.repository.MeterRepository;
import ru.tipsauk.monitoring.repository.jdbcRepositoryImpl.JdbcMeterRepositoryImpl;
import ru.tipsauk.monitoring.service.in.CollectionServicImpl.CollectionMeterServiceImpl;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.TreeSet;

class CollectionMeterServiceImplTest {

    private CollectionMeterServiceImpl collectionMeterService;
    private User user;
    private Meter meter;

    @BeforeEach
    void setUp() {
        collectionMeterService = new CollectionMeterServiceImpl();
        user = new User("user", "123", UserRole.USER);
        meter = new Meter("Счетчик");
    }

    @Test
    @DisplayName("Передача показаний")
    void transmitMeterValue_Successful() {
        assertThat(collectionMeterService.transmitMeterValue(user, meter, 100)).isTrue();
        assertThat(user.getUserActions().values())
                .anyMatch(action -> action.getAction() == UserActionType.TRANSMIT_VALUES);
    }

    @Test
    @DisplayName("Дублирование показаний по счетчику")
    void transmitMeterValue_DuplicateTransmission() {
        MeterValue meterValue = new MeterValue(LocalDate.now());
        meterValue.addMeterReading(meter, 50);
        user.addMeterValueToUser(LocalDate.now(), meterValue);
        assertThat(collectionMeterService.transmitMeterValue(user, meter, 200)).isFalse();
        assertThat(user.getUserActions().values())
                .noneMatch(action -> action.getAction() == UserActionType.TRANSMIT_VALUES);
    }

    @Test
    @DisplayName("Получение последних показаний")
    void getValueMeter_ValidDate() {
        LocalDate dateValue = LocalDate.now();
        user.addMeterValueToUser(dateValue, new MeterValue(LocalDate.now()));
        MeterValue result = collectionMeterService.getValueMeter(user, dateValue);
        assertThat(result).isNotNull();
        assertThat(user.getUserActions().values())
                .anyMatch(action -> action.getAction() == UserActionType.GETTING_VALUES);

    }

    @Test
    @DisplayName("Получение показаний за предыдущие периоды")
    void getValueMeter_InvalidDate() {
        MeterValue result = collectionMeterService.getValueMeter(user, LocalDate.now().minusMonths(1));
        assertThat(result).isNotNull();
        assertThat(user.getUserActions().values())
                .anyMatch(action -> action.getAction() == UserActionType.GETTING_VALUES);
    }

    @Test
    @DisplayName("Получение последних показаний если не передавались")
    void getLastValueMeter_NoValues() {
        MeterValue result = collectionMeterService.getLastValueMeter(user);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Получение последних показаний")
    void getLastValueMeter_WithValues() {
        MeterValue meterValue = new MeterValue(LocalDate.now());
        user.addMeterValueToUser(LocalDate.now(), meterValue);
        MeterValue result = collectionMeterService.getLastValueMeter(user);
        assertThat(result).isSameAs(meterValue);
    }

    @Test
    @DisplayName("Получение истории показаний")
    void getValueMeterHistory() {
        MeterValue meterValue1 = new MeterValue(LocalDate.now());
        MeterValue meterValue2 = new MeterValue(LocalDate.now().minusMonths(1));
        MeterValue meterValue3 = new MeterValue(LocalDate.now().minusMonths(2));
        user.addMeterValueToUser(LocalDate.now(), meterValue1);
        user.addMeterValueToUser(LocalDate.now().minusMonths(1), meterValue2);
        user.addMeterValueToUser(LocalDate.now().minusMonths(2), meterValue3);
        TreeSet<MeterValue> result = collectionMeterService.getValueMeterHistory(user);
        assertThat(result).hasSize(3);
        assertThat(result.first()).isEqualTo(meterValue1);
    }

}