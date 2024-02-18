package ru.tipsauk.monitoring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tipsauk.monitoring.dto.UserActionDto;
import ru.tipsauk.monitoring.model.UserAction;

/**
 * Интерфейс-маппер для преобразования между сущностью UserAction и DTO UserActionDto.
 */
@Mapper(componentModel = "spring")
public interface UserActionMapper {

    @Mapping(source = "timeAction", target = "timeAction")
    @Mapping(source = "action", target = "action")
    @Mapping(source = "description", target = "description")
    UserActionDto userActionToUserActionDto(UserAction userAction);

    @Mapping(source = "timeAction", target = "timeAction")
    @Mapping(source = "action", target = "action")
    @Mapping(source = "description", target = "description")
    UserAction userActionDtoToUserAction(UserActionDto userActionDto);
}
