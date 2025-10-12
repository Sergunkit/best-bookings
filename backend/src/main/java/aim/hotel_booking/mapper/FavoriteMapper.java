package aim.hotel_booking.mapper;

import aim.hotel_booking.entity.HotelEntity;
import org.openapitools.model.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FavoriteMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "stars", source = "stars")
    @Mapping(target = "rating", source = "rating")
    @Mapping(target = "photoSrc", source = "photoSrc")
    Hotel toDto(HotelEntity entity);
}
