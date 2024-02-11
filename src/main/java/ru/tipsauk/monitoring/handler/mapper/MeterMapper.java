package ru.tipsauk.monitoring.handler.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.tipsauk.monitoring.dto.MeterDto;
import ru.tipsauk.monitoring.model.Meter;

@Mapper
public interface MeterMapper {

    MeterMapper INSTANCE = Mappers.getMapper(MeterMapper.class);

    @Mapping(source = "name", target = "name")
    MeterDto meterToMeterDto(Meter meter);

    @Mapping(source = "name", target = "name")
    Meter meterDtoToMeter(MeterDto meterDto);

}
