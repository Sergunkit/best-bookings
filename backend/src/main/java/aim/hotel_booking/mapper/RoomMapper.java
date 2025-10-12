package aim.hotel_booking.mapper;

import aim.hotel_booking.entity.RoomEntity;
import aim.hotel_booking.entity.RoomAmenityEntity;
import aim.hotel_booking.entity.AmenityEntity;
import org.openapitools.model.RoomCreateDto;
import org.openapitools.model.RoomDto;
import org.openapitools.model.RoomUpdateDto;
import org.openapitools.model.Amenity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AmenityMapper.class})
public interface RoomMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "hotel", ignore = true)
    RoomEntity toEntity(RoomCreateDto dto);

    @Mapping(target = "amenities", source = "amenities", qualifiedByName = "mapAmenities")
    @Mapping(target = "hotelId", source = "hotel.id")
    RoomDto toDto(RoomEntity entity);

    List<RoomDto> toDtoList(List<RoomEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "hotel", ignore = true)
    void updateFromDto(RoomUpdateDto dto, @MappingTarget RoomEntity entity);

    @Named("mapAmenities")
    default List<Amenity> mapAmenities(List<RoomAmenityEntity> roomAmenities) {
        if (roomAmenities == null) {
            return null;
        }
        return roomAmenities.stream()
                .map(roomAmenity -> {
                    AmenityEntity amenityEntity = roomAmenity.getAmenity();
                    Amenity amenity = new Amenity();
                    amenity.setId(amenityEntity.getId());
                    amenity.setName(amenityEntity.getName());
                    return amenity;
                })
                .toList();
    }
}
