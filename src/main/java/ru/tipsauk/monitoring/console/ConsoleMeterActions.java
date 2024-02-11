package ru.tipsauk.monitoring.console;

import ru.tipsauk.monitoring.model.Meter;
import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.service.in.MeterService;

import java.util.Scanner;
import java.util.Set;

/**
 * Класс, представляющий действия с счетчиками в консоли.
 */
public class ConsoleMeterActions {

    /**
     * Сервис для работы с счетчиками.
     */
    private final MeterService meterService;

    /**
     * Сканер для ввода с консоли.
     */
    private final Scanner scanner;

    /**
     * Конструктор класса ConsoleMeterActions.
     *
     * @param meterService Сервис для работы с счетчиками.
     * @param scanner      Сканер для ввода с консоли.
     */
    public ConsoleMeterActions(MeterService meterService, Scanner scanner) {
        this.meterService = meterService;
        this.scanner = scanner;
    }

    /**
     * Метод для добавления нового счетчика через консоль.
     *
     * @param currentUser Текущий авторизованный пользователь.
     */
    public void consoleAddNewMeter(User currentUser) {
        if (!currentUser.isUserAdministrator()) {
            return;
        }
        outPutMeters();
        System.out.println("Введите имя нового счетчика:");
        scanner.nextLine();
        String nameMeter = scanner.nextLine();
        if (nameMeter.isEmpty()) {
            System.out.println("Введено пустое имя!");
            return;
        }
        meterService.addNewMeter(nameMeter);
        System.out.println("Новый вид счетчика успешно добавлен!");
    }

    /**
     * Вывод списка текущих счетчиков в консоль.
     */
    private void outPutMeters() {
        Set<Meter> meters = meterService.getAllMeters();
        System.out.println("Текущие счетчики:");
        meters.forEach(m ->  System.out.println(m.getName()));
    }
}
