package ru.tipsauk.monitoring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tipsauk.monitoring.dto.MeterDto;
import ru.tipsauk.monitoring.model.Meter;

/**
 * Интерфейс-маппер для преобразования между сущностью Meter и DTO MeterDto.
 */
@Mapper(componentModel = "spring")
public interface MeterMapper {

    @Mapping(source = "name", target = "name")
    MeterDto meterToMeterDto(Meter meter);

    @Mapping(source = "name", target = "name")
    Meter meterDtoToMeter(MeterDto meterDto);

}
