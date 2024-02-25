package ru.tipsauk.monitoring.service.in.DatabaseServiceImpl;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import ru.tipsauk.monitoring.dto.MeterDto;
import ru.tipsauk.monitoring.dto.MeterValueDto;
import ru.tipsauk.monitoring.model.*;
import ru.tipsauk.monitoring.service.in.MeterService;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class DatabaseMeterServiceImplTest {

    @Container
    private static final PostgreSQLContainer<?> SQLContainer
            = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("monitoring_service")
            .withUsername("monitoring")
            .withPassword("monitoring");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", SQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", SQLContainer::getPassword);
        registry.add("spring.datasource.username", SQLContainer::getUsername);
    }
    @Autowired
    private MeterService meterService;

    private static User user;

    private static Meter meter;

    private static Meter meter2;

    private static MeterValueDto meterValueDto;


    @BeforeAll
    static void setUp() {
        try  {
            YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
            yaml.setResources(new ClassPathResource("application-test.yml"));
            Properties properties = yaml.getObject();
            SQLContainer.start();
            Connection connection = DriverManager.getConnection(SQLContainer.getJdbcUrl()
                    , SQLContainer.getUsername(), SQLContainer.getPassword());
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase(properties.getProperty("spring.liquibase.change-log"),
                    new ClassLoaderResourceAccessor(), database);
            liquibase.update("test");
            connection.close();

            user = new User(1,"user", "123", UserRole.USER);
            meter = new Meter(1,"Счетчик горячей воды");
            meter2 = new Meter(2,"Счетчик холодной воды");
            meterValueDto = new MeterValueDto();
            meterValueDto.setDateValue(new Date());
            meterValueDto.addMeterReading(meter, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    @DisplayName("Тест на получение всех счетчиков")
    void getAllMeters_shouldReturnMeters() {
        Set<MeterDto> allMeters = meterService.getAllMeters();
        assertThat(allMeters).isNotNull();
        assertThat(allMeters.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("Тест на успешную передачу показаний")
    void transmitMeterValue_Successful() {
        Meter meter3 = new Meter(2,"Счетчик отопления");
        MeterValueDto meterValueDto3 = new MeterValueDto();
        meterValueDto3.setDateValue(new Date());
        meterValueDto3.addMeterReading(meter3, 100);
        assertThat(meterService.transmitMeterValueWeb(user, meterValueDto3)).isTrue();
    }

    @Test
    @DisplayName("Тест на получение последних показаний")
    void getValueMeter_ValidDate() {
        LocalDate dateValue = LocalDate.now().withDayOfMonth(1);
        MeterValueDto result = meterService.getValueMeter(user, dateValue);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Тест на получение показаний за предыдущие периоды")
    void getValueMeter_InvalidDate() {
        meterService.transmitMeterValueWeb(user, meterValueDto);
        MeterValueDto result = meterService.getValueMeter(user, LocalDate.now().minusMonths(1));
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Тест на получение последних показаний если не передавались")
    void getLastValueMeter_NoValues() {
        MeterValueDto result = meterService.getLastValueMeter(user);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Тест на получение истории показаний")
    void getValueMeterHistory() {
        MeterValueDto meterValueDto2 = new MeterValueDto();
        meterValueDto2.setDateValue(new Date());
        meterValueDto2.addMeterReading(meter2, 200);
        meterService.transmitMeterValueWeb(user, meterValueDto2);
        TreeSet<MeterValueDto> result = meterService.getValueMeterHistory(user);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Тест на получение последних показаний")
    void getLastValueMeter_WithValues() {
        MeterValueDto result = meterService.getLastValueMeter(user);
        assertThat(result).isNotNull();
    }

}