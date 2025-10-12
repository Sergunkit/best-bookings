package aim.hotel_booking.mapper;

import aim.hotel_booking.entity.UserEntity;
import org.openapitools.model.UserCreateDto;
import org.openapitools.model.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    UserEntity toEntity(UserCreateDto dto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    UserDto toDto(UserEntity entity);

    // Добавляем метод для преобразования списка
    List<UserDto> toDtoList(List<UserEntity> entities);

    default UserEntity toEntityWithPassword(UserCreateDto dto, PasswordEncoder encoder) {
        UserEntity entity = toEntity(dto);
        entity.setPassword(encoder.encode(dto.getPassword()));
        return entity;
    }
}
