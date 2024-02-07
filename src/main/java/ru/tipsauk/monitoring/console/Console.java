package ru.tipsauk.monitoring.console;

import ru.tipsauk.monitoring.service.in.MeterService;
import ru.tipsauk.monitoring.service.in.UserService;

import java.util.*;

/**
 * Основной класс, представляющий консольное приложение для управления приема
 * и контроля ввода показанияий счетчиков.
 */
public class Console {

    /** Сервис для работы с пользователями. */
    private final UserService userService;

    /** Сервис для работы с показаниями счетчиков. */
    private final MeterService meterService;

    /** Сканер для ввода с консоли. */
    private final Scanner scanner = new Scanner(System.in);

    public Console(UserService userService, MeterService meterService) {
        this.userService = userService;
        this.meterService = meterService;
    }
    public void run() {

        ConsoleUserActions consoleUserActions = new ConsoleUserActions(userService, scanner);
        ConsoleMeterActions consoleMeterActions = new ConsoleMeterActions(meterService, scanner);
        ConsoleMeterReadings consoleMeterReadings = new ConsoleMeterReadings(meterService, scanner);
        ConsoleMenu consoleMenu = new ConsoleMenu(consoleUserActions, consoleMeterActions, consoleMeterReadings, scanner);
        consoleMenu.mainConsole();
    }
}
