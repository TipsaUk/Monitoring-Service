package ru.tipsauk.monitoring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.tipsauk.monitoring.dto.MeterDto;
import ru.tipsauk.monitoring.dto.MeterValueDto;
import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.model.UserRole;
import ru.tipsauk.monitoring.service.in.MeterService;
import ru.tipsauk.monitoring.service.in.UserService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class MeterControllerTest {

    private MockMvc mockMvc;

    private String username;

    private  User user;

    @Mock
    private MeterService meterService;

    @Mock
    private UserService userService;

    @InjectMocks
    private MeterController meterController;

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(meterController).build();
        this.username = "user";
        this.user = new User("user", "pass", UserRole.USER);
    }

    @Test
    @DisplayName("MockMvc тест на успешное добавление нового счетчика")
    void testAddNewMeter() throws Exception {
        MeterDto meterDto = new MeterDto("meter");
        Mockito.when(meterService.addNewMeter(meterDto.getName())).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/meter/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(meterDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Successful addition!"));
    }

    @Test
    @DisplayName("Тест на успешное добавление нового счетчика")
    void addNewMeter_Success() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        MeterDto meterDto = new MeterDto("meterName");
        when(meterService.addNewMeter("meterName")).thenReturn(true);
        ResponseEntity<String> response = meterController.addNewMeter(meterDto, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Successful addition!");
    }

    @Test
    @DisplayName("Тест на ошибку при добавлении нового счетчика")
    void addNewMeter_Error() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        MeterDto meterDto = new MeterDto("meterName");
        when(meterService.addNewMeter("meterName")).thenReturn(false);
        ResponseEntity<String> response = meterController.addNewMeter(meterDto, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Error");
    }

    @Test
    @DisplayName("MockMvc тест на успешную передачу значения счетчика")
    void testTransmitValue() throws Exception {
        MeterValueDto meterValueDto = new MeterValueDto();
        when(userService.getUserBySessionId(any())).thenReturn(user);
        when(meterService.transmitMeterValueWeb(user, meterValueDto)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/meter/transmit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(meterValueDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Successful addition!"));
    }

    @Test
    @DisplayName("Тест на успешную передачу значения счетчика")
    void transmitValue_Success() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(userService.getUserBySessionId(any())).thenReturn(user);
        MeterValueDto meterValueDto = new MeterValueDto();
        when(meterService.transmitMeterValueWeb(user, meterValueDto)).thenReturn(true);
        ResponseEntity<String> response = meterController.transmitValue(meterValueDto, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Successful addition!");
    }

    @Test
    @DisplayName("Тест на ошибку при передаче значения счетчика")
    void transmitValue_Error() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(userService.getUserBySessionId(any())).thenReturn(user);
        MeterValueDto meterValueDto = new MeterValueDto();
        when(meterService.transmitMeterValueWeb(user, meterValueDto)).thenReturn(false);
        ResponseEntity<String> response = meterController.transmitValue(meterValueDto, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Error");
    }

    @Test
    @DisplayName("MockMvc тест на успешное получение всех счетчиков")
    void testOutPutMeters() throws Exception {
        Set<MeterDto> mockMeterSet = new HashSet<>();
        Mockito.when(meterService.getAllMeters()).thenReturn(mockMeterSet);
        mockMvc.perform(MockMvcRequestBuilders.get("/meter/meters")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Тест на успешное получение всех счетчиков")
    void outPutMeters_Success() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Set<MeterDto> mockedMeterDtoSet = new HashSet<>();
        when(meterService.getAllMeters()).thenReturn(mockedMeterDtoSet);
        ResponseEntity<Set<MeterDto>> response = meterController.outPutMeters(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(mockedMeterDtoSet);
    }

    @Test
    @DisplayName("MockMvc тест на успешное получение значений счетчика по дате")
    void testGetMeterValues() throws Exception {
        LocalDate dateValue = LocalDate.now();
        MeterValueDto mockMeterValueDto = new MeterValueDto();
        Mockito.when(userService.getUserByName(username)).thenReturn(user);
        Mockito.when(meterService.getValueMeter(user, dateValue)).thenReturn(mockMeterValueDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/meter/value")
                        .param("dateValue", dateValue.toString())
                        .param("username", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Тест на успешное получение значений счетчика по дате")
    void getMeterValues_Success() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        LocalDate dateValue = LocalDate.now();
        when(userService.getUserByName(username)).thenReturn(user);
        MeterValueDto mockedMeterValueDto = new MeterValueDto();
        when(meterService.getValueMeter(user, dateValue)).thenReturn(mockedMeterValueDto);
        ResponseEntity<MeterValueDto> response = meterController.getMeterValues(dateValue, username, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(mockedMeterValueDto);
    }

    @Test
    @DisplayName("MockMvc тест на успешное получение актуальных значений счетчика")
    void testGetActualMeterValues() throws Exception {
        MeterValueDto mockMeterValueDto = new MeterValueDto();
        Mockito.when(userService.getUserByName(username)).thenReturn(user);
        Mockito.when(meterService.getLastValueMeter(user)).thenReturn(mockMeterValueDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/meter/actual-value")
                        .param("username", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Тест на успешное получение актуальных значений счетчика")
    void getActualMeterValues_Success() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(userService.getUserByName(username)).thenReturn(user);
        MeterValueDto mockedMeterValueDto = new MeterValueDto();
        when(meterService.getLastValueMeter(user)).thenReturn(mockedMeterValueDto);
        ResponseEntity<MeterValueDto> response = meterController.getActualMeterValues(username, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(mockedMeterValueDto);
    }

    @Test
    @DisplayName("MockMvc тест на успешное получение истории значений счетчика")
    void testGetValueMeterHistory() throws Exception {
        TreeSet<MeterValueDto> mockMeterValueHistory = new TreeSet<>();
        Mockito.when(userService.getUserByName(username)).thenReturn(user);
        Mockito.when(meterService.getValueMeterHistory(user)).thenReturn(mockMeterValueHistory);
        mockMvc.perform(MockMvcRequestBuilders.get("/meter/values")
                        .param("username", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Тест на успешное получение истории значений счетчика")
    void getValueMeterHistory_Success() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(userService.getUserByName(username)).thenReturn(user);
        TreeSet<MeterValueDto> mockedMeterValueHistory = new TreeSet<>(); 
        when(meterService.getValueMeterHistory(user)).thenReturn(mockedMeterValueHistory);
        ResponseEntity<TreeSet<MeterValueDto>> response = meterController.getValueMeterHistory(username, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(mockedMeterValueHistory);
    }
}