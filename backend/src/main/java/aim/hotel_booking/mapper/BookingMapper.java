package aim.hotel_booking.mapper;

import aim.hotel_booking.entity.BookingEntity;
import org.openapitools.model.Booking;
import org.openapitools.model.BookingCreateDto;
import org.openapitools.model.BookingUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BookingMapper {
    
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "room.id", target = "roomId")
    @Mapping(source = "checkInDate", target = "checkIn")
    @Mapping(source = "checkOutDate", target = "checkOut")
    Booking toDto(BookingEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "roomId", target = "room.id")
    @Mapping(source = "checkIn", target = "checkInDate")
    @Mapping(source = "checkOut", target = "checkOutDate")
    BookingEntity toEntity(BookingCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "roomId", target = "room.id")
    @Mapping(source = "checkIn", target = "checkInDate")
    @Mapping(source = "checkOut", target = "checkOutDate")
    void updateEntity(BookingUpdateDto dto, @MappingTarget BookingEntity entity);
}
