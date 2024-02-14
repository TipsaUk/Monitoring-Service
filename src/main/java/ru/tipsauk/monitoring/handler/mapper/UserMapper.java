package ru.tipsauk.monitoring.handler.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.tipsauk.monitoring.dto.UserDto;
import ru.tipsauk.monitoring.model.User;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "nickName", target = "nickName")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "password", target = "password", ignore = true)
    UserDto userToUserDto(User user);

    @Mapping(source = "nickName", target = "nickName")
    @Mapping(source = "role", target = "role")
    User userDtoToUser(UserDto userDto);
}
