package ru.tipsauk.monitoring.handler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import ru.tipsauk.monitoring.dto.MeterValueDto;
import ru.tipsauk.monitoring.handler.mapper.MeterValueMapper;
import ru.tipsauk.monitoring.handler.util.RequestUtils;
import ru.tipsauk.monitoring.model.MeterValue;
import ru.tipsauk.monitoring.model.User;

import ru.tipsauk.monitoring.model.UserRole;
import ru.tipsauk.monitoring.service.in.MeterService;
import ru.tipsauk.monitoring.service.in.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.TreeSet;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MeterHandlerTest {

    @Mock
    private MeterService meterService;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private MeterHandler meterHandler;

    @Mock
    private MeterValueMapper meterValueMapper;


    @Test
    @DisplayName("Тест на добавление нового счетчика")
    void testAddNewMeterSuccess() throws IOException {
        String jsonRequestBody = "{\"name\":\"new meter\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequestBody)));
        when(request.getContentType()).thenReturn("application/json");
        when(RequestUtils.getCurrentSessionId(request)).thenReturn("sessionId");
        when(userService.getUserBySessionId("sessionId")).thenReturn(new User());
        when(RequestUtils.isDtoValid(any())).thenReturn(true);
        when(meterService.addNewMeter(anyString())).thenReturn(true);
        meterHandler.addNewMeter(request, response);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response.getWriter()).write(anyString());
    }

    @Test
    @DisplayName("Тест на получение списка счетчиков")
    void testOutPutMetersSuccess() throws Exception {
        when(meterService.getAllMeters()).thenReturn(new HashSet<>());
        meterHandler.outPutMeters(request, response);
        verify(response).setStatus(HttpServletResponse.SC_OK);

    }

    @Test
    @DisplayName("Тест на передачу показаний счетчиков")
    void testTransmitValueSuccess() throws Exception {
        User user = new User("user", "123", UserRole.USER);
        String jsonRequestBody = "{\"dateValue\":\"2024-02-01\",\"meterValues\":{\"new meter\":100,\"}}";
        when(RequestUtils.isContentTypeJson(request)).thenReturn(true);
        when(userService.getUserBySessionId(any())).thenReturn(user);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequestBody)));
        when(meterService.transmitMeterValueWeb(any(), any(), anyInt())).thenReturn(true);
        meterHandler.transmitValue(request, response);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(meterService).transmitMeterValueWeb(user, "new meter", 100);
    }

    @Test
    @DisplayName("Тест на получение показаний счетчиков")
    public void testGetMeterValues() {
        User user = new User("user", "123", UserRole.USER);
        when(userService.getUserBySessionId(any())).thenReturn(user);
        when(request.getParameter("dateValue")).thenReturn("2024-02-01");
        when(meterService.getValueMeter(any(), any())).thenReturn(new MeterValue(LocalDate.of(2024, 2, 1)));
        meterHandler.getMeterValues(request, response);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Тест на получение актуальных показаний счетчиков")
    public void testGetActualMeterValues() throws IOException {
        User user = new User("user", "123", UserRole.USER);
        MeterValue meterValue = mock(MeterValue.class);
        MeterValueDto meterValueDto = mock(MeterValueDto.class);
        when(RequestUtils.getUserForGettingData(userService, request, response)).thenReturn(user);
        when(meterService.getLastValueMeter(user)).thenReturn(meterValue);
        when(meterValueMapper.meterValueToMeterValueDto(meterValue)).thenReturn(meterValueDto);
        when(meterValueDto.getDateValue()).thenReturn(new Date());
        meterHandler.getActualMeterValues(request, response);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Тест на получение истории получения показаний счетчиков")
    public void testGetValueMeterHistory()  {
        User user = new User("user", "123", UserRole.USER);
        MeterValue meterValue = mock(MeterValue.class);
        MeterValueDto meterValueDto = mock(MeterValueDto.class);
        when(RequestUtils.getUserForGettingData(userService, request, response)).thenReturn(user);
        when(meterService.getValueMeterHistory(user)).thenReturn((TreeSet<MeterValue>) Collections.singleton(meterValue));
        when(meterValueMapper.meterValueToMeterValueDto(meterValue)).thenReturn(meterValueDto);
        when(meterValueDto.getDateValue()).thenReturn(new Date());
        meterHandler.getValueMeterHistory(request, response);
        verify(response, never()).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }
}