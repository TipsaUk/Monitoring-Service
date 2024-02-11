package ru.tipsauk.monitoring.handler.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.tipsauk.monitoring.dto.UserActionDto;
import ru.tipsauk.monitoring.model.UserAction;

@Mapper
public interface UserActionMapper {

    UserActionMapper INSTANCE = Mappers.getMapper(UserActionMapper.class);

    @Mapping(source = "timeAction", target = "timeAction")
    @Mapping(source = "action", target = "action")
    @Mapping(source = "description", target = "description")
    UserActionDto userActionToUserActionDto(UserAction userAction);

    @Mapping(source = "timeAction", target = "timeAction")
    @Mapping(source = "action", target = "action")
    @Mapping(source = "description", target = "description")
    UserAction userActionDtoToUserAction(UserActionDto userActionDto);
}
