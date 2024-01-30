package ru.tipsauk.monitoring;

import ru.tipsauk.monitoring.service.in.MeterService;
import ru.tipsauk.monitoring.service.in.UserService;
import ru.tipsauk.monitoring.model.Meter;
import ru.tipsauk.monitoring.model.MeterValue;
import ru.tipsauk.monitoring.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Основной класс, представляющий консольное приложение для управления приема
 * и контроля ввода показанияий счетчиков.
 */
public class Console {

    /** Сервис для работы с пользователями. */
    private static final UserService userService = new UserService();

    /** Сервис для работы с показаниями счетчиков. */
    private static final MeterService meterService = new MeterService();

    /** Сканер для ввода с консоли. */
    private static final Scanner scanner = new Scanner(System.in);

    /** Список счетчиков. */
    private static List<Meter> meters = new ArrayList<>();

    static {
        meters.add(new Meter("Счетчик горячей воды"));
        meters.add(new Meter("Счетчик холодной воды"));
        meters.add(new Meter("Счетчик отопления"));
    }

    /**
     * Основной метод, для работы консольного приложения (регистрация, вход пользователя).
     */
    public static void mainConsole() {
        while (true) {
            outPutMainActions();
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> consoleSignUp();
                case 2 -> consoleSignIn();
                case 3 -> System.exit(0);
                default -> System.out.println("Некорректный выбор");
            }
            meterValueConsole();
        }
    }

    /**
     * Основной метод, для работы консольного приложения (показания счетчиков).
     */
    private static void meterValueConsole() {
        while (true) {
            User currentUser = userService.getSessionUser();
            if (currentUser == null) {
                break;
            }
            outPutMeterValueActions(currentUser);
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> consoleSignOut();
                case 2 -> consoleTransmitValue(currentUser);
                case 3 -> consoleGetActualMeterValues(currentUser);
                case 4 -> consoleGetMeterValues(currentUser);
                case 5 -> consoleGetValueMeterHistory(currentUser);
                case 6 -> consoleAddNewMeter(currentUser);
                case 7 -> consoleGetUserActions(currentUser);
                default -> System.out.println("Некорректный выбор");
            }
        }
    }

    /**
     * Метод для отображения действий в консоли.
     */
    private static void outPutMainActions() {
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
    private static void outPutMeterValueActions(User currentUser) {
        System.out.println(System.lineSeparator());
        System.out.println("Текущий пользователь: " + currentUser.getNickName());
        System.out.println("Возможные действия:");
        System.out.println("1. Выход из системы");
        System.out.println("2. Передача показаний счетчика");
        System.out.println("3. Просмотр актуальных показаний");
        System.out.println("4. Просмотр показаний за предыдущий период");
        System.out.println("5. Просмотр истории показаний");
        if (userService.isUserAdministrator(currentUser)) {
            System.out.println("6. Добавление нового вида счетчика");
            System.out.println("7. Просмотр действий пользователя");
        }
        System.out.println("Выберите действие:");
    }

    /**
     * Метод для регистрации пользователя в консоли.
     */
    private static void consoleSignUp() {
        System.out.println("Введите имя пользователя:");
        String nickName = scanner.next();
        System.out.println("Введите пароль:");
        String password = scanner.next();
        if (userService.signUp(nickName, password)) {
            System.out.println("Регистрация успешна!");
        }
    }

    /**
     * Метод для входа в систему пользователя в консоли.
     */
    private static void consoleSignIn() {
        System.out.println("Введите имя пользователя:");
        String nickName = scanner.next();
        System.out.println("Введите пароль:");
        String password = scanner.next();
        if (userService.signIn(nickName, password)) {
            System.out.println("Вход выполнен успешно!");
        }
    }

    /**
     * Метод для выхода из системы пользователя в консоли.
     */
    private static void consoleSignOut() {
        userService.signOut();
        System.out.println("Выход выполнен успешно!");
    }

    /**
     * Метод для передачи показания счетчика в консоли.
     *
     * @param currentUser текущий пользователь консоли
     */
    private static void consoleTransmitValue(User currentUser) {
        System.out.println("Выберите счетчик:");
        for (int i = 0; i < meters.size(); i++) {
            System.out.println(i + ". " + meters.get(i).getName());
        }
        int number = scanner.nextInt();
        if (number >= meters.size()) {
            System.out.println("Не верный номер счетчика!");
            return;
        }
        System.out.println("Введите показание счетчика:");
        if (meterService.transmitMeterValue(currentUser, meters.get(number), scanner.nextInt())) {
            System.out.println("Показания успешно переданы!");
        }
    }

    /**
     * Метод для получения показаний счетчиков за определенный месяц в консоли.
     *
     * @param currentUser текущий пользователь консоли
     */
    private static void consoleGetMeterValues(User currentUser) {
        User user = (userService.isUserAdministrator(currentUser)) ? consoleInputUser() : currentUser;
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
    private static LocalDate consoleInputDate() {
        while (true) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.MM.yyyy");
            System.out.println("Введите месяц показаний в формате (месяц.год):");
            String month = scanner.next();
            try {
                return LocalDate.parse("1." + month, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Не верный формат даты!");
            }
        }
    }

    /**
     * Метод для выбора пользователя в консоли (только для администратора).
     *
     * @return выбранный пользователь.
     */
    private static User consoleInputUser() {
        System.out.println("Пользователи:");
        userService.getAllUsers().forEach(u ->System.out.println(u.getNickName()));
        System.out.println("Введите имя пользователя для получения данных:");
        while (true) {
            User user = userService.getUserByName(scanner.next());
            if (user != null) {
                return user;
            }
            System.out.println("Пользователя с таким именем не существует!");
        }
    }

    /**
     * Метод для получения актуальных показаний счетчиков (последние введенные показания) в консоли.
     *
     * @param currentUser текущий пользователь консоли
     */
    private static void consoleGetActualMeterValues(User currentUser) {
        User user = (userService.isUserAdministrator(currentUser)) ? consoleInputUser() : currentUser;
        HashMap<Meter, Integer> meterValues = meterService.getLastValueMeter(user).getMeterValues();
        System.out.println("Актуальные показания счетчиков:");
        for (Map.Entry<Meter, Integer> entry : meterValues.entrySet()) {
            System.out.println(entry.getKey().getName() + ": " + entry.getValue());
        }
    }

    /**
     * Метод для получения истории показаний счетчиков в консоли.
     *
     * @param currentUser текущий пользователь консоли
     */
    private static void consoleGetValueMeterHistory(User currentUser) {
        User user = (userService.isUserAdministrator(currentUser)) ? consoleInputUser() : currentUser;
        TreeSet<MeterValue> meterValues = meterService.getValueMeterHistory(user);
        meterValues.forEach(meterValueMonth -> {
            System.out.println(meterValueMonth.getDateValue());
            for (Map.Entry<Meter, Integer> entry : meterValueMonth.getMeterValues().entrySet()) {
                System.out.println(" " + entry.getKey() + ": " + entry.getValue());
            }
        });
    }

    /**
     * Метод для ввода нового типа счетчика в консоли (только для администратора).
     *
     * @param currentUser текущий пользователь консоли
     */
    private static void consoleAddNewMeter(User currentUser) {
        if (!userService.isUserAdministrator(currentUser)) {
            return;
        }
        System.out.println("Текущие счетчики:");
        for (int i = 0; i < meters.size(); i++) {
            System.out.println(i + ". " + meters.get(i).getName());
        }
        System.out.println("Введите имя нового счетчика:");
        meters.add(new Meter(scanner.next()));
        System.out.println("Новый вид счетчика успешно добавлен!");
    }

    /**
     * Метод получение действий пользователя в системе (только для администратора).
     *
     * @param currentUser текущий пользователь консоли
     */
    private static void consoleGetUserActions(User currentUser) {
        if (!userService.isUserAdministrator(currentUser)) {
            return;
        }
        userService.getUserActions(consoleInputUser(), null)
                .forEach(a->System.out.println(a.getTimeAction() + " -> " + a.getAction() + " " + a.getDescription()));
    }

}
