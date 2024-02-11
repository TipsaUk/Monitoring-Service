package ru.tipsauk.monitoring.service.in.DatabaseServiceImpl;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tipsauk.monitoring.config.ApplicationConfig;
import ru.tipsauk.monitoring.model.*;
import ru.tipsauk.monitoring.repository.MeterRepository;
import ru.tipsauk.monitoring.repository.MeterValueRepository;
import ru.tipsauk.monitoring.repository.UserActionRepository;
import ru.tipsauk.monitoring.repository.jdbcRepositoryImpl.JdbcMeterRepositoryImpl;
import ru.tipsauk.monitoring.repository.jdbcRepositoryImpl.JdbcMeterValueRepository;
import ru.tipsauk.monitoring.repository.jdbcRepositoryImpl.JdbcUserActionRepositoryImpl;
import ru.tipsauk.monitoring.service.in.MeterService;

import java.sql.*;
import java.time.LocalDate;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class DatabaseMeterServiceImplTest {

    @Container
    private static final PostgreSQLContainer<?> SQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("monitoring_service")
            .withUsername("monitoring")
            .withPassword("monitoring");
    private static MeterService meterService;

    private static User user;

    private static Meter meter;

    private static Meter meter2;

    @BeforeAll
    static void setUp() {
        try  {
            SQLContainer.start();
            Properties liquibaseProperties = new Properties();
            liquibaseProperties.load(DatabaseMeterServiceImplTest.class
                    .getClassLoader().getResourceAsStream("application.properties"));
            Connection connection = DriverManager.getConnection(SQLContainer.getJdbcUrl()
                    , SQLContainer.getUsername(), SQLContainer.getPassword());
            String changeLogFile = liquibaseProperties.getProperty("changeLogFile");
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase(changeLogFile,
                    new ClassLoaderResourceAccessor(), database);
            liquibase.update("test");
            connection.close();

            ApplicationConfig config = new ApplicationConfig(SQLContainer.getJdbcUrl()
                    , SQLContainer.getUsername(), SQLContainer.getPassword());
            MeterRepository meterRepository = new JdbcMeterRepositoryImpl(config);
            MeterValueRepository meterValueRepository = new JdbcMeterValueRepository(config);
            UserActionRepository userActionRepository = new JdbcUserActionRepositoryImpl(config);
            meterService = new DatabaseMeterServiceImpl(meterRepository
                    , meterValueRepository, userActionRepository);

            user = new User(1,"user", "123", UserRole.USER);
            meter = new Meter(1,"Счетчик горячей воды");
            meter2 = new Meter(2,"Счетчик холодной воды");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    @DisplayName("Получение счетчиков")
    void getAllMeters_shouldReturnMeters() {
        Set<Meter> allMeters = meterService.getAllMeters();
        assertThat(allMeters).isNotNull();
        assertThat(allMeters.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("Передача показаний")
    void transmitMeterValue_Successful() {
        assertThat(meterService.transmitMeterValue(user, meter2, 100)).isTrue();
    }

    @Test
    @DisplayName("Получение последних показаний")
    void getValueMeter_ValidDate() {
        LocalDate dateValue = LocalDate.now().withDayOfMonth(1);
        MeterValue result = meterService.getValueMeter(user, dateValue);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Получение показаний за предыдущие периоды")
    void getValueMeter_InvalidDate() {
        meterService.transmitMeterValue(user, meter, 100);
        MeterValue result = meterService.getValueMeter(user, LocalDate.now().minusMonths(1));
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Получение последних показаний если не передавались")
    void getLastValueMeter_NoValues() {
        MeterValue result = meterService.getLastValueMeter(user);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Получение истории показаний")
    void getValueMeterHistory() {
        MeterValue meterValue = new MeterValue(LocalDate.now());
        meterValue.addMeterReading(meter, 100);
        meterService.transmitMeterValue(user, meter, 100);
        meterService.transmitMeterValue(user, meter2, 200);
//        TreeSet<MeterValue> result = meterService.getValueMeterHistory(user);
//        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Получение последних показаний")
    void getLastValueMeter_WithValues() {
        MeterValue meterValue = new MeterValue(LocalDate.now());
        meterValue.addMeterReading(meter, 100);
        meterValue.addMeterReading(meter2, 200);
        meterService.transmitMeterValue(user, meter, 100);
        meterService.transmitMeterValue(user, meter2, 200);
        MeterValue result = meterService.getLastValueMeter(user);
//        assertThat(result.getDateValue()).isEqualTo(meterValue.getDateValue());
//        assertThat(result.getMeterValues().size()).isEqualTo(meterValue.getMeterValues().size());

    }

}