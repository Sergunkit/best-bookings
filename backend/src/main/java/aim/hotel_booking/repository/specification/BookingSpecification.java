package aim.hotel_booking.repository.specification;

import aim.hotel_booking.entity.BookingEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class BookingSpecification {

    public Specification<BookingEntity> hasUserId(Integer userId) {
        return (root, query, cb) -> 
            userId != null ? cb.equal(root.get("user").get("id"), userId) : null;
    }

    public Specification<BookingEntity> hasRoomId(Integer roomId) {
        return (root, query, cb) -> 
            roomId != null ? cb.equal(root.get("room").get("id"), roomId) : null;
    }

    public Specification<BookingEntity> checkInBefore(OffsetDateTime date) {
        return (root, query, cb) -> 
            date != null ? cb.lessThanOrEqualTo(root.get("checkInDate"), date) : null;
    }

    public Specification<BookingEntity> checkInAfter(OffsetDateTime date) {
        return (root, query, cb) -> 
            date != null ? cb.greaterThanOrEqualTo(root.get("checkInDate"), date) : null;
    }

    public Specification<BookingEntity> checkOutBefore(OffsetDateTime date) {
        return (root, query, cb) -> 
            date != null ? cb.lessThanOrEqualTo(root.get("checkOutDate"), date) : null;
    }

    public Specification<BookingEntity> checkOutAfter(OffsetDateTime date) {
        return (root, query, cb) -> 
            date != null ? cb.greaterThanOrEqualTo(root.get("checkOutDate"), date) : null;
    }

    public static Specification<BookingEntity> datesOverlap(
        Integer roomId,
        OffsetDateTime checkIn,
        OffsetDateTime checkOut,
        Integer excludeBookingId
    ) {
        return (root, query, cb) -> {
            // Проверяем, что номера совпадают
            var roomPredicate = cb.equal(root.get("room").get("id"), roomId);

            // Проверяем пересечение дат
            var datesOverlapPredicate = cb.and(
                cb.lessThan(root.get("checkInDate"), checkOut),
                cb.greaterThan(root.get("checkOutDate"), checkIn)
            );

            // Если нужно исключить определенное бронирование
            if (excludeBookingId != null) {
                var excludePredicate = cb.notEqual(root.get("id"), excludeBookingId);
                return cb.and(roomPredicate, datesOverlapPredicate, excludePredicate);
            }

            return cb.and(roomPredicate, datesOverlapPredicate);
        };
    }
}
