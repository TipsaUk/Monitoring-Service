package ru.tipsauk.monitoring;

import ru.tipsauk.monitoring.config.ApplicationConfig;
import ru.tipsauk.monitoring.console.Console;
import ru.tipsauk.monitoring.repository.MeterRepository;
import ru.tipsauk.monitoring.repository.MeterValueRepository;
import ru.tipsauk.monitoring.repository.UserActionRepository;
import ru.tipsauk.monitoring.repository.UserRepository;
import ru.tipsauk.monitoring.repository.jdbcRepositoryImpl.JdbcMeterRepositoryImpl;
import ru.tipsauk.monitoring.repository.jdbcRepositoryImpl.JdbcMeterValueRepository;
import ru.tipsauk.monitoring.repository.jdbcRepositoryImpl.JdbcUserActionRepositoryImpl;
import ru.tipsauk.monitoring.repository.jdbcRepositoryImpl.JdbcUserRepositoryImpl;
import ru.tipsauk.monitoring.service.in.DatabaseServiceImpl.DatabaseMeterServiceImpl;
import ru.tipsauk.monitoring.service.in.DatabaseServiceImpl.DatabaseUserServiceImpl;
import ru.tipsauk.monitoring.service.in.MeterService;
import ru.tipsauk.monitoring.service.in.UserService;

public class Application {

    /**
     * Главный метод, который запускает консольное приложение для ввода данных по счетчикам.
     * Программа предоставляет возможность регистрации, входа в систему и взаимодействия с показаниями счетчиков.
     *
     * @param args Аргументы командной строки (не используются в данном приложении).
     */
    public static void main(String[] args) {

//        ApplicationConfig config = new ApplicationConfig();
//        MeterRepository meterRepository = new JdbcMeterRepositoryImpl(config);
//        MeterValueRepository meterValueRepository = new JdbcMeterValueRepository(config);
//        UserRepository userRepository = new JdbcUserRepositoryImpl(config);
//        UserActionRepository userActionRepository = new JdbcUserActionRepositoryImpl(config);
        //  Закоментированы классы реализующие работу программы с коллекциями без использования базы данных
        //  UserService userService = new CollectionUserServiceImpl();
        //  MeterService meterService = new CollectionMeterServiceImpl();
//        UserService userService = new DatabaseUserServiceImpl(userRepository, userActionRepository);
//        MeterService meterService = new DatabaseMeterServiceImpl(meterRepository
//                                                , meterValueRepository, userActionRepository);
//        Console console = new Console(userService, meterService);
//        console.run();
    }

}

