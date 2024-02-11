package ru.tipsauk.monitoring.servlet.in;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.tipsauk.monitoring.annotations.Loggable;
import ru.tipsauk.monitoring.aspect.UserAuditAspect;
import ru.tipsauk.monitoring.config.ApplicationConfig;
import ru.tipsauk.monitoring.handler.MeterHandler;
import ru.tipsauk.monitoring.handler.mapper.MeterMapper;
import ru.tipsauk.monitoring.handler.mapper.MeterValueMapper;
import ru.tipsauk.monitoring.repository.MeterRepository;
import ru.tipsauk.monitoring.repository.MeterValueRepository;
import ru.tipsauk.monitoring.repository.UserActionRepository;
import ru.tipsauk.monitoring.repository.jdbcRepositoryImpl.JdbcMeterRepositoryImpl;
import ru.tipsauk.monitoring.repository.jdbcRepositoryImpl.JdbcMeterValueRepository;
import ru.tipsauk.monitoring.repository.jdbcRepositoryImpl.JdbcUserActionRepositoryImpl;
import ru.tipsauk.monitoring.service.in.DatabaseServiceImpl.DatabaseWebMeterServiceImpl;
import ru.tipsauk.monitoring.service.in.MeterService;
import ru.tipsauk.monitoring.service.in.UserService;

import java.io.IOException;

/**
 * Сервлет для обработки запросов, связанных с счетчиками.
 * Этот сервлет обрабатывает запросы на получение списка счетчиков, текущих показаний счетчика,
 * истории показаний счетчика, а также запросы на добавление нового счетчика и передачу показаний счетчика.
 */
@Loggable
public class MeterServlet extends HttpServlet{

    /**
     * Обработчик запросов, связанных с счетчиками.
     */
    private MeterHandler meterHandler;

    /**
     * Метод инициализации сервлета, вызывается при его создании.
     *
     * @param config Конфигурация сервлета.
     * @throws ServletException Исключение, возникающее при ошибках инициализации сервлета.
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ApplicationConfig appConfig = new ApplicationConfig();
        MeterRepository meterRepository = new JdbcMeterRepositoryImpl(appConfig);
        MeterValueRepository meterValueRepository = new JdbcMeterValueRepository(appConfig);
        UserActionRepository userActionRepository = new JdbcUserActionRepositoryImpl(appConfig);
        MeterService meterService = new DatabaseWebMeterServiceImpl(meterRepository, meterValueRepository);
        UserService userService = (UserService) config.getServletContext().getAttribute("userService");
        MeterMapper meterMapper = MeterMapper.INSTANCE;
        MeterValueMapper meterValueMapper = MeterValueMapper.INSTANCE;
        meterHandler = new MeterHandler(meterService, userService, meterMapper, meterValueMapper);
        UserAuditAspect.setUserService(userService);
        UserAuditAspect.setUserActionRepository(userActionRepository);
    }

    /**
     * Метод обрабатывает HTTP GET-запросы.
     *
     * @param req  Запрос от клиента.
     * @param resp Ответ сервера.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        switch (req.getPathInfo()) {
            case "/meters" -> meterHandler.outPutMeters(req, resp);
            case "/meter_values" -> meterHandler.getMeterValues(req, resp);
            case "/actual_meter_values" -> meterHandler.getActualMeterValues(req, resp);
            case "/meter_history" -> meterHandler.getValueMeterHistory(req, resp);
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Метод обрабатывает HTTP POST-запросы.
     *
     * @param req  Запрос от клиента.
     * @param resp Ответ сервера.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        switch (req.getPathInfo()) {
            case "/add" -> meterHandler.addNewMeter(req, resp);
            case "/transmit" -> meterHandler.transmitValue(req, resp);
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}
