package ru.tipsauk.monitoring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tipsauk.monitoring.dto.MeterDto;
import ru.tipsauk.monitoring.dto.MeterValueDto;
import ru.tipsauk.monitoring.exception.NotFoundException;
import ru.tipsauk.monitoring.util.RequestUtils;

import ru.tipsauk.monitoring.model.User;
import ru.tipsauk.monitoring.service.in.MeterService;
import ru.tipsauk.monitoring.service.in.UserService;
import ru.tipsauk.user_audit_starter.annotations.UserAudit;


import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

@Tag(
        name = "API для работы с счетчиками",
        description = "API для управления показаниями счетчиков (прием показаний, получение и др.)"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("meter")
public class MeterController {

    /**
     * Сервис для работы с счетчиками.
     */
    private final MeterService meterService;

    /**
     * Сервис для работы с пользователями.
     */
    private final UserService userService;

    @Operation(
            summary = "Добавление нового счетчика",
            description = "API для добавления нового счетчика",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное добавление"),
                    @ApiResponse(responseCode = "400", description = "Ошибка")
            }
    )
    @PostMapping("/add")
    @UserAudit(actionType = "ADDING_SYSTEM_INFO")
    public ResponseEntity<String> addNewMeter(@Valid @RequestBody MeterDto meterDto, HttpServletRequest request) {
        return  meterService.addNewMeter(meterDto.getName())
                ? ResponseEntity.ok("Successful addition!")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error");
    }

    @Operation(
            summary = "Передача значений счетчика",
            description = "API для передачи значений счетчика",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешная передача"),
                    @ApiResponse(responseCode = "400", description = "Ошибка")
            }
    )
    @PostMapping("/transmit")
    @UserAudit(actionType = "TRANSMIT_VALUES")
    public ResponseEntity<String> transmitValue(@Valid @RequestBody MeterValueDto meterValueDto, HttpServletRequest request) {
        User currentUser = userService.getUserBySessionId(RequestUtils.getCurrentSessionId(request));
        return  meterService.transmitMeterValueWeb(currentUser, meterValueDto)
                ? ResponseEntity.ok("Successful addition!")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error");
    }

    @Operation(
            summary = "Получение списка всех счетчиков",
            description = "API для получения списка всех счетчиков",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение",
                            content = {@Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = MeterDto.class)))
                    })
            }
    )
    @GetMapping("/meters")
    @UserAudit(actionType = "GETTING_SYSTEM_INFO")
    public ResponseEntity<Set<MeterDto>> outPutMeters(HttpServletRequest request) {
        return ResponseEntity.ok(meterService.getAllMeters());
    }

    @Operation(
            summary = "Получение значений счетчика по дате",
            description = "API для получения значений счетчика по дате",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение",
                            content = {@Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = MeterValueDto.class)))
                            })
            }
    )
    @GetMapping("/value")
    @UserAudit(actionType = "GETTING_VALUES")
    public ResponseEntity<MeterValueDto> getMeterValues(
            @Parameter(description = "Дата показаний") LocalDate dateValue,
            @Parameter(description = "Имя пользователя") String username,
            HttpServletRequest request) {
        User user = userService.getUserByName(username);
        if (user == null) {
            throw new NotFoundException("User not found: " + username);
        }
        return ResponseEntity.ok(meterService.getValueMeter(user, dateValue));
    }

    @Operation(
            summary = "Получение актуальных значений счетчика",
            description = "API для получения актуальных значений счетчика",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение",
                            content = {@Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = MeterValueDto.class)))
                            })
            }
    )
    @GetMapping("/actual-value")
    @UserAudit(actionType = "GETTING_VALUES")
    public ResponseEntity<MeterValueDto> getActualMeterValues(
            @Parameter(description = "Имя пользователя") String username, HttpServletRequest request) {
        User user = userService.getUserByName(username);
        if (user == null) {
            throw new NotFoundException("User not found: " + username);
        }
        MeterValueDto meterValueDto = meterService.getLastValueMeter(user);
        return  ResponseEntity.ok(meterValueDto != null
                ? meterValueDto
                : new MeterValueDto());
    }

    @Operation(
            summary = "Получение истории значений счетчика",
            description = "API для получения истории значений счетчика",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение",
                            content = {@Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = MeterValueDto.class)))
                            })
            }
    )
    @GetMapping("/values")
    @UserAudit(actionType = "GETTING_VALUES")
    public ResponseEntity<TreeSet<MeterValueDto>> getValueMeterHistory(String username, HttpServletRequest request) {
        User user = userService.getUserByName(username);
        if (user == null) {
            throw new NotFoundException("User not found: " + username);
        }
        return ResponseEntity.ok(meterService.getValueMeterHistory(user));
    }

}
