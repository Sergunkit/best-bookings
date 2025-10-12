package aim.hotel_booking.repository;

import aim.hotel_booking.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends
        JpaRepository<RoomEntity, Integer>,
        JpaSpecificationExecutor<RoomEntity> {

    List<RoomEntity> findByHotelId(Integer hotelId);
    boolean existsByNameAndHotelId(String name, Integer hotelId);
}
