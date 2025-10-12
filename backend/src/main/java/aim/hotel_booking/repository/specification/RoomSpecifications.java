package aim.hotel_booking.repository.specification;

import aim.hotel_booking.entity.RoomEntity;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;

public class RoomSpecifications {

    public static Specification<RoomEntity> hasHotelId(Integer hotelId) {
        return (root, query, cb) ->
                cb.equal(root.get("hotel").get("id"), hotelId);
    }

    public static Specification<RoomEntity> hasNameLike(String name) {
        return (root, query, cb) ->
                name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<RoomEntity> hasPriceGreaterThanOrEqual(BigDecimal minPrice) {
        return (root, query, cb) ->
                minPrice == null ? null : cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<RoomEntity> hasPriceLessThanOrEqual(BigDecimal maxPrice) {
        return (root, query, cb) ->
                maxPrice == null ? null : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }
}
