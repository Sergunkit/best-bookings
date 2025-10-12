package aim.hotel_booking.mapper;

import aim.hotel_booking.entity.HotelEntity;
import org.mapstruct.MappingTarget;
import org.openapitools.model.Hotel;
import org.openapitools.model.HotelCreateDto;
import org.openapitools.model.HotelUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface HotelMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rating", source = "rating", qualifiedByName = "setDefaultRating")
    HotelEntity toEntity(HotelCreateDto dto);

    @Mapping(target = "rating", source = "rating", qualifiedByName = "setDefaultRating")
    @Mapping(target = "id", ignore = true)
    HotelEntity updateFromDto(HotelUpdateDto dto, @MappingTarget HotelEntity entity);

    @Mapping(target = "rating", source = "rating", qualifiedByName = "formatRating")
    Hotel toDto(HotelEntity entity);

    List<Hotel> toDtoList(List<HotelEntity> entities);

    @Named("setDefaultRating")
    default BigDecimal setDefaultRating(BigDecimal rating) {
        return rating != null ? rating : BigDecimal.valueOf(0);
    }

    @Named("formatRating")
    default BigDecimal formatRating(BigDecimal rating) {
        return rating != null ? rating.setScale(2, java.math.RoundingMode.HALF_UP) : null;
    }
}
