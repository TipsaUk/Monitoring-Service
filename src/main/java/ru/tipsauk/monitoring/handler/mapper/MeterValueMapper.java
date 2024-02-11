package ru.tipsauk.monitoring.handler.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.tipsauk.monitoring.dto.MeterValueDto;
import ru.tipsauk.monitoring.model.MeterValue;

@Mapper
public interface MeterValueMapper {

    MeterValueMapper INSTANCE = Mappers.getMapper(MeterValueMapper.class);

    @Mapping(source = "dateValue", target = "dateValue")
    @Mapping(source = "meterValues", target = "meterValues")
    MeterValueDto meterValueToMeterValueDto(MeterValue meterValue);

    @Mapping(source = "dateValue", target = "dateValue")
    @Mapping(source = "meterValues", target = "meterValues")
    MeterValue meterValueDtoToMeterValue(MeterValueDto meterValueDto);

}
