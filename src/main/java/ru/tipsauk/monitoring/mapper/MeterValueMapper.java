package ru.tipsauk.monitoring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tipsauk.monitoring.dto.MeterValueDto;
import ru.tipsauk.monitoring.model.MeterValue;

/**
 * Интерфейс-маппер для преобразования между сущностью MeterValue и DTO MeterValueDto.
 */
@Mapper(componentModel = "spring")
public interface MeterValueMapper {

    @Mapping(source = "dateValue", target = "dateValue")
    @Mapping(source = "meterValues", target = "meterValues")
    MeterValueDto meterValueToMeterValueDto(MeterValue meterValue);

    @Mapping(source = "dateValue", target = "dateValue")
    @Mapping(source = "meterValues", target = "meterValues")
    MeterValue meterValueDtoToMeterValue(MeterValueDto meterValueDto);

}
