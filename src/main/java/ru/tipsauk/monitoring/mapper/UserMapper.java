package ru.tipsauk.monitoring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tipsauk.monitoring.dto.UserDto;
import ru.tipsauk.monitoring.model.User;

/**
 * Интерфейс-маппер для преобразования между сущностью User и DTO UserDto.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "nickName", target = "nickName")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "password", target = "password", ignore = true)
    UserDto userToUserDto(User user);

    @Mapping(source = "nickName", target = "nickName")
    @Mapping(source = "role", target = "role")
    User userDtoToUser(UserDto userDto);
}
