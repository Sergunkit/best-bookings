package aim.hotel_booking.repository;

import aim.hotel_booking.entity.AmenityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmenityRepository extends JpaRepository<AmenityEntity, Integer> {
}
