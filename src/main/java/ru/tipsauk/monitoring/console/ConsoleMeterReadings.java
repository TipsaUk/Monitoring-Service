package ru.tipsauk.monitoring.console;

import ru.tipsauk.monitoring.model.Meter;
import ru.tipsauk.monitoring.model.MeterValue;
import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.service.in.MeterService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Класс, представляющий действия с показаниями счетчиков в консоли.
 */
public class ConsoleMeterReadings {

    /**
     * Сервис для работы с счетчиками.
     */
    private final MeterService meterService;

    /**
     * Сканер для ввода с консоли.
     */
    private final Scanner scanner;

    /**
     * Конструктор класса ConsoleMeterReadings.
     *
     * @param meterService Сервис для работы с счетчиками.
     * @param scanner      Сканер для ввода с консоли.
     */
    public ConsoleMeterReadings(MeterService meterService, Scanner scanner) {
        this.meterService = meterService;
        this.scanner = scanner;
    }

    /**
     * Метод для передачи показания счетчика в консоли.
     *
     * @param currentUser текущий пользователь консоли
     */
    public void consoleTransmitValue(User currentUser) {
        List<Meter> meters = meterService.getAllMeters().stream().toList();
        System.out.println("Выберите счетчик:");
        for (int i = 0; i < meters.size(); i++) {
            System.out.println(i + ". " + meters.get(i).getName());
        }
        try {
            int number = scanner.nextInt();
            if (number >= meters.size()) {
                System.out.println("Не верный номер счетчика!");
                return;
            }
            System.out.println("Введите показание счетчика:");
            int value = scanner.nextInt();
            if (value < 0) {
                System.out.println("Введено отрицательное значение!");
                return;
            }
            if (meterService.transmitMeterValue(currentUser, meters.get(number), value)) {
                System.out.println("Показания успешно переданы!");
            }
        } catch (InputMismatchException e) {
            System.out.println("Введено не числовое значение!");
        }
    }

    /**
     * Метод для получения показаний счетчиков за определенный месяц в консоли.
     *
     * @param user текущий пользователь консоли
     */
    public void consoleGetMeterValues(User user) {
        LocalDate inputDate = consoleInputDate();
        HashMap<Meter, Integer> meterValues =
                meterService.getValueMeter(user, inputDate).getMeterValues();
        System.out.println("Показания счетчиков за " + inputDate);
        for (Map.Entry<Meter, Integer> entry : meterValues.entrySet()) {
            System.out.println(entry.getKey().getName() + ": " + entry.getValue());
        }
    }

    /**
     * Метод для ввода даты в консоли в формате (месяц.год).
     *
     * @return выбранная дата.
     */
    private LocalDate consoleInputDate() {
        while (true) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.MM.yyyy");
            System.out.println("Введите месяц показаний в формате (месяц.год):");
            String month = scanner.next();
            try {
                return LocalDate.parse("1." + month, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Не верный формат даты!");
                scanner.nextLine();
            }
        }
    }

    /**
     * Метод для получения актуальных показаний счетчиков (последние введенные показания) в консоли.
     *
     * @param user текущий пользователь консоли
     */
    public void consoleGetActualMeterValues(User user) {
        MeterValue meterValue = meterService.getLastValueMeter(user);
        if (meterValue == null) {
            return;
        }
        HashMap<Meter, Integer> meterValues = meterValue.getMeterValues();
        System.out.println("Актуальные показания счетчиков:");
        for (Map.Entry<Meter, Integer> entry : meterValues.entrySet()) {
            System.out.println(entry.getKey().getName() + ": " + entry.getValue());
        }
    }

    /**
     * Метод для получения истории показаний счетчиков в консоли.
     *
     * @param user текущий пользователь консоли
     */
    public void consoleGetValueMeterHistory(User user) {
        TreeSet<MeterValue> meterValues = meterService.getValueMeterHistory(user);
        meterValues.forEach(meterValueMonth -> {
            System.out.println(meterValueMonth.getDateValue());
            for (Map.Entry<Meter, Integer> entry : meterValueMonth.getMeterValues().entrySet()) {
                System.out.println(" " + entry.getKey() + ": " + entry.getValue());
            }
        });
    }
}
