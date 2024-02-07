package ru.tipsauk.monitoring.console;

import ru.tipsauk.monitoring.model.User;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Класс, представляющий консольное меню приложения.
 */
public class ConsoleMenu {

    /**
     * Объект для выполнения действий с пользователем в консоли.
     */
    private final ConsoleUserActions consoleUserActions;

    /**
     * Объект для выполнения действий со счетчиками в консоли.
     */
    private final ConsoleMeterActions consoleMeterActions;

    /**
     * Объект для выполнения действий с показаниями счетчиков в консоли.
     */
    private final ConsoleMeterReadings consoleMeterReadings;

    /**
     * Сканер для ввода с консоли.
     */
    private final Scanner scanner;

    /**
     * Конструктор класса ConsoleMenu.
     *
     * @param consoleUserActions     Объект для выполнения действий с пользователем в консоли.
     * @param consoleMeterActions    Объект для выполнения действий со счетчиками в консоли.
     * @param consoleMeterReadings   Объект для выполнения действий с показаниями счетчиков в консоли.
     * @param scanner                Сканер для ввода с консоли.
     */
    public ConsoleMenu(ConsoleUserActions consoleUserActions, ConsoleMeterActions consoleMeterActions
            , ConsoleMeterReadings consoleMeterReadings, Scanner scanner) {
        this.consoleUserActions = consoleUserActions;
        this.consoleMeterActions = consoleMeterActions;
        this.consoleMeterReadings = consoleMeterReadings;
        this.scanner = scanner;
    }

    /**
     * Основной метод, для работы консольного приложения (регистрация, вход пользователя).
     */
    public void mainConsole() {
        while (true) {
            outPutMainActions();
            try {
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1 -> consoleUserActions.consoleSignUp();
                    case 2 -> consoleUserActions.consoleSignIn();
                    case 3 -> System.exit(0);
                    default -> System.out.println("Некорректный выбор");
                }
            } catch (InputMismatchException e) {
                System.out.println("Введите числовое значение!");
                scanner.nextLine();
            }
            meterValueConsole();
        }
    }

    /**
     * Основной метод, для работы консольного приложения (показания счетчиков).
     */
    private void meterValueConsole() {
        while (true) {
            User currentUser = consoleUserActions.getSessionUser();
            if (currentUser == null) {
                break;
            }
            outPutMeterValueActions(currentUser);
            try {
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1 -> consoleUserActions.consoleSignOut();
                    case 2 -> consoleMeterReadings.consoleTransmitValue(currentUser);
                    case 3 -> consoleMeterReadings
                            .consoleGetActualMeterValues(consoleUserActions.getUserForGettingInformation());
                    case 4 -> consoleMeterReadings
                            .consoleGetMeterValues(consoleUserActions.getUserForGettingInformation());
                    case 5 -> consoleMeterReadings
                            .consoleGetValueMeterHistory(consoleUserActions.getUserForGettingInformation());
                    case 6 -> consoleMeterActions.consoleAddNewMeter(currentUser);
                    case 7 -> consoleUserActions.consoleGetUserActions(currentUser);
                    default -> System.out.println("Некорректный выбор");
                }
            } catch (InputMismatchException e) {
                System.out.println("Введите числовое значение!");
                scanner.nextLine();
            }
        }
    }

    /**
     * Метод для отображения действий в консоли.
     */
    private void outPutMainActions() {
        System.out.println("Возможные действия:");
        System.out.println("1. Регистрация пользователя");
        System.out.println("2. Вход в систему");
        System.out.println("3. Завершить работу системы"
                + System.lineSeparator());
        System.out.println("Выберите действие:");
    }

    /**
     * Метод для отображения действий в консоли.
     *
     * @param currentUser текущий пользователь консоли
     */
    private void outPutMeterValueActions(User currentUser) {
        System.out.println(System.lineSeparator());
        System.out.println("Текущий пользователь: " + currentUser.getNickName());
        System.out.println("Возможные действия:");
        System.out.println("1. Выход из системы");
        System.out.println("2. Передача показаний счетчика");
        System.out.println("3. Просмотр актуальных показаний");
        System.out.println("4. Просмотр показаний за предыдущий период");
        System.out.println("5. Просмотр истории показаний");
        if (currentUser.isUserAdministrator()) {
            System.out.println("6. Добавление нового вида счетчика");
            System.out.println("7. Просмотр действий пользователя");
        }
        System.out.println("Выберите действие:");
    }

}
