package aim.hotel_booking.repository;

import aim.hotel_booking.entity.HotelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends
        JpaRepository<HotelEntity, Integer>,
        JpaSpecificationExecutor<HotelEntity> {

    boolean existsByName(String name);
}
