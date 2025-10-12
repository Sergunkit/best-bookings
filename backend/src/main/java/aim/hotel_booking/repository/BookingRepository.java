package aim.hotel_booking.repository;

import aim.hotel_booking.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Integer>,
        JpaSpecificationExecutor<BookingEntity> {
}
