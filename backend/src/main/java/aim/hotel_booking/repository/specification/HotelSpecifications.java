package aim.hotel_booking.repository.specification;

import aim.hotel_booking.entity.HotelEntity;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;

public class HotelSpecifications {

    public static Specification<HotelEntity> hasNameLike(String name) {
        return (root, query, cb) ->
                name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<HotelEntity> hasRatingGreaterThanOrEqual(BigDecimal minRating) {
        return (root, query, cb) ->
                minRating == null ? null : cb.greaterThanOrEqualTo(root.get("rating"), minRating);
    }

    public static Specification<HotelEntity> hasRatingLessThanOrEqual(BigDecimal maxRating) {
        return (root, query, cb) ->
                maxRating == null ? null : cb.lessThanOrEqualTo(root.get("rating"), maxRating);
    }

    public static Specification<HotelEntity> hasStarsGreaterThanOrEqual(Integer minStars) {
        return (root, query, cb) ->
                minStars == null ? null : cb.greaterThanOrEqualTo(root.get("stars"), minStars);
    }

    public static Specification<HotelEntity> hasStarsLessThanOrEqual(Integer maxStars) {
        return (root, query, cb) ->
                maxStars == null ? null : cb.lessThanOrEqualTo(root.get("stars"), maxStars);
    }
}
