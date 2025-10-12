package aim.hotel_booking.mapper;

import aim.hotel_booking.entity.UserEntity;
import aim.hotel_booking.service.AuthenticationService;
import org.openapitools.model.TokenInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class TokenMapper {
    
    @Autowired
    protected AuthenticationService authenticationService;

    @Mapping(target = "token", expression =
            "java(authenticationService.authenticate(user.getEmail(), user.getPassword()))")
    public abstract TokenInfo toDto(UserEntity user);
}
