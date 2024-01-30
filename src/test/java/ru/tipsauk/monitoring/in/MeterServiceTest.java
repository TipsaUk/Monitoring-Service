package ru.tipsauk.monitoring.in;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.tipsauk.monitoring.model.*;
import ru.tipsauk.monitoring.service.in.MeterService;

import java.time.LocalDate;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class MeterServiceTest {

    private MeterService meterService;
    private User user;
    private Meter meter;

    @BeforeEach
    void setUp() {
        meterService = new MeterService();
        user = new User("user", "123", UserRole.USER);
        meter = new Meter("—четчик");
    }

    @Test
    void transmitMeterValue_Successful() {
        assertTrue(meterService.transmitMeterValue(user, meter, 100));
        assertTrue(user.getUserActions().values().stream()
                .anyMatch(action -> action.getAction() == UserActionType.TRANSMIT_VALUES));
    }

    @Test
    void transmitMeterValue_DuplicateTransmission() {
        MeterValue meterValue = new MeterValue();
        meterValue.addMeterValue(meter, 50);
        user.addValueToUser(LocalDate.now(), meterValue);

        assertFalse(meterService.transmitMeterValue(user, meter, 200));
        assertFalse(user.getUserActions().values().stream()
                .anyMatch(action -> action.getAction() == UserActionType.TRANSMIT_VALUES));
    }

    @Test
    void getValueMeter_ValidDate() {
        LocalDate dateValue = LocalDate.now();
        user.addValueToUser(dateValue, new MeterValue());
        assertNotNull(meterService.getValueMeter(user, dateValue));
        assertTrue(user.getUserActions().values().stream()
                .anyMatch(action -> action.getAction() == UserActionType.GETTING_VALUES));
    }

    @Test
    void getValueMeter_InvalidDate() {
        MeterValue result = meterService.getValueMeter(user, LocalDate.now().minusMonths(1));
        assertNotNull(result);
        assertTrue(user.getUserActions().values().stream()
                .anyMatch(action -> action.getAction() == UserActionType.GETTING_VALUES));
    }

    @Test
    void getLastValueMeter_NoValues() {
        MeterValue result = meterService.getLastValueMeter(user);
        assertNotNull(result);
    }

    @Test
    void getLastValueMeter_WithValues() {
        MeterValue meterValue = new MeterValue();
        user.addValueToUser(LocalDate.now(), meterValue);
        MeterValue result = meterService.getLastValueMeter(user);
        assertSame(meterValue, result);
    }

    @Test
    void getValueMeterHistory() {
        MeterValue meterValue1 = new MeterValue(LocalDate.now());
        MeterValue meterValue2 = new MeterValue(LocalDate.now().minusMonths(1));
        MeterValue meterValue3 = new MeterValue(LocalDate.now().minusMonths(2));
        user.addValueToUser(LocalDate.now(), meterValue1);
        user.addValueToUser(LocalDate.now().minusMonths(1), meterValue2);
        user.addValueToUser(LocalDate.now().minusMonths(2), meterValue3);
        TreeSet<MeterValue> result = meterService.getValueMeterHistory(user);
        assertEquals(3, result.size());
        assertEquals(result.first(), meterValue1);
    }

}