package ru.tipsauk.monitoring.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import ru.tipsauk.monitoring.annotations.UserAudit;
import ru.tipsauk.monitoring.dto.MeterDto;
import ru.tipsauk.monitoring.dto.ApiResponse;
import ru.tipsauk.monitoring.dto.MeterValueDto;
import ru.tipsauk.monitoring.handler.mapper.MeterMapper;
import ru.tipsauk.monitoring.handler.mapper.MeterValueMapper;
import ru.tipsauk.monitoring.model.*;
import ru.tipsauk.monitoring.service.in.MeterService;
import ru.tipsauk.monitoring.handler.util.RequestUtils;
import ru.tipsauk.monitoring.service.in.UserService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс MeterHandler предназначен для обработки запросов, связанных с объектами Meter и MeterValue.
 * Класс содержит методы для выполнения различных операций с счетчиками и их показаниями.
 */
public class MeterHandler {

    /**
     * Сервис для работы с счетчиками.
     */
    private final MeterService meterService;

    /**
     * Сервис для работы с пользователями.
     */
    private final UserService userService;

    /**
     * Объект для преобразования объектов в JSON.
     */
    private final ObjectMapper objectMapper;

    /**
     * Маппер для преобразования объектов Meter в объекты DTO (Data Transfer Object).
     */
    private final MeterMapper meterMapper;

    /**
     * Маппер для преобразования объектов MeterValue в объекты DTO (Data Transfer Object).
     */
    private final MeterValueMapper meterValueMapper;

    /**
     * Конструктор класса MeterHandler.
     *
     * @param meterService      Сервис для работы с счетчиками.
     * @param userService       Сервис для работы с пользователями.
     * @param meterMapper       Маппер для преобразования объектов Meter в DTO.
     * @param meterValueMapper  Маппер для преобразования объектов MeterValue в DTO.
     */
    public MeterHandler(MeterService meterService, UserService userService,
                        MeterMapper meterMapper, MeterValueMapper meterValueMapper) {
        this.meterService = meterService;
        this.objectMapper = new ObjectMapper();
        this.meterMapper = meterMapper;
        this.meterValueMapper = meterValueMapper;
        this.userService = userService;
    }

    /**
     * Метод для добавления нового счетчика.
     *
     * @param request  Запрос от клиента.
     * @param response Ответ сервера.
     */
    public void addNewMeter(HttpServletRequest request, HttpServletResponse response) {
        try {
            User currentUser = userService.getUserBySessionId(RequestUtils.getCurrentSessionId(request));
            if (currentUser.getRole() != UserRole.ADMINISTRATOR) {
                response.setStatus(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);
                return;
            }
            if (!RequestUtils.isContentTypeJson(request)) {
                response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
                return;
            }
            String jsonString = RequestUtils.extractJsonFromRequest(request);
            MeterDto meterDto = objectMapper.readValue(jsonString, MeterDto.class);
            if (!RequestUtils.isDtoValid(meterDto)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            ApiResponse apiResponse = meterService.addNewMeter(meterDto.getName())
                    ? new ApiResponse("success", "Успешно добавлен новый счетчик")
                    : new ApiResponse("error", "Внутренняя ошибка при добавлении счетчика");
            RequestUtils.setResponse(response, objectMapper.writeValueAsString(apiResponse));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Метод для получения списка всех счетчиков.
     *
     * @param request  Запрос от клиента.
     * @param response Ответ сервера.
     * @throws IOException Исключение, возникающее при работе с вводом/выводом.
     */
    @UserAudit(actionType = UserActionType.GETTING_VALUES)
    public void outPutMeters(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String jsonResponse = objectMapper.writeValueAsString(meterService.getAllMeters().stream()
                    .map(meterMapper::meterToMeterDto)
                    .collect(Collectors.toSet()));
            RequestUtils.setResponse(response, jsonResponse);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Метод для передачи показаний счетчика.
     *
     * @param request  Запрос от клиента.
     * @param response Ответ сервера.
     */
    @UserAudit(actionType = UserActionType.TRANSMIT_VALUES)
    public void transmitValue(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (!RequestUtils.isContentTypeJson(request)) {
                response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
                return;
            }
            User currentUser = userService.getUserBySessionId(RequestUtils.getCurrentSessionId(request));
            String jsonString = RequestUtils.extractJsonFromRequest(request);
            MeterValueDto meterValueDto = objectMapper.readValue(jsonString, MeterValueDto.class);
            if (!RequestUtils.isDtoValid(meterValueDto)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            MeterValue meterValue = meterValueMapper.meterValueDtoToMeterValue(meterValueDto);
            Optional<Map.Entry<Meter, Integer>> entryMeterValue = meterValue.getMeterValues().entrySet().stream().findFirst();
            if (entryMeterValue.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            ApiResponse apiResponse = meterService.transmitMeterValueWeb(currentUser
                    , entryMeterValue.get().getKey().getName()
                    , entryMeterValue.get().getValue())
                    ? new ApiResponse("success", "Показания успешно добавлены!")
                    : new ApiResponse("error", "Ошибка при добавлении показаний");
            RequestUtils.setResponse(response, objectMapper.writeValueAsString(apiResponse));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Метод для получения показаний счетчика по дате.
     *
     * @param request  Запрос от клиента.
     * @param response Ответ сервера.
     */
    @UserAudit(actionType = UserActionType.GETTING_VALUES)
    public void getMeterValues(HttpServletRequest request, HttpServletResponse response) {
        try {
            String dateValueString = request.getParameter("dateValue");
            if (dateValueString == null || dateValueString.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dateValue = LocalDate.parse(dateValueString, formatter);
            User user = RequestUtils.getUserForGettingData(userService, request, response);
            if (user == null) {
                return;
            }
            MeterValue meterValue = meterService.getValueMeter(user, dateValue);
            setMeterValueDtoToResponse (meterValue, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Метод для получения актуальных показаний счетчика.
     *
     * @param request  Запрос от клиента.
     * @param response Ответ сервера.
     */
    @UserAudit(actionType = UserActionType.GETTING_VALUES)
    public void getActualMeterValues(HttpServletRequest request, HttpServletResponse response) {
        User user = RequestUtils.getUserForGettingData(userService, request, response);
        if (user == null) {
            return;
        }
        try {
            MeterValue meterValue = meterService.getLastValueMeter(user);
            setMeterValueDtoToResponse(meterValue, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Метод для получения истории показаний счетчика.
     *
     * @param request  Запрос от клиента.
     * @param response Ответ сервера.
     */
    @UserAudit(actionType = UserActionType.GETTING_VALUES)
    public void getValueMeterHistory(HttpServletRequest request, HttpServletResponse response) {
        User user = RequestUtils.getUserForGettingData(userService, request, response);
        if (user == null) {
            return;
        }
        try {
            Set<MeterValueDto> meterValues =
            meterService.getValueMeterHistory(user).stream()
                    .map(meterValueMapper::meterValueToMeterValueDto)
                    .collect(Collectors.toSet());
            String jsonResponse = objectMapper.writeValueAsString(meterValues);
            RequestUtils.setResponse(response, jsonResponse);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Вспомогательный метод для установки DTO показаний счетчика в ответ сервера.
     *
     * @param meterValue Объект MeterValue с показаниями счетчика.
     * @param response   Ответ сервера.
     * @throws IOException
     */
    private void setMeterValueDtoToResponse (MeterValue meterValue, HttpServletResponse response) throws IOException {
        MeterValueDto meterValueDto = meterValue != null
                ? meterValueMapper.meterValueToMeterValueDto(meterValue)
                : new MeterValueDto();
        String jsonResponse = objectMapper.writeValueAsString(meterValueDto);
        RequestUtils.setResponse(response, jsonResponse);
    }

}
