package aim.hotel_booking.repository.specification;

import aim.hotel_booking.entity.FavoriteEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class FavoriteSpecification {

    // Базовые спецификации

    public static Specification<FavoriteEntity> withUserId(Integer userId) {
        return (root, query, cb) ->
                userId == null ? null : cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<FavoriteEntity> withHotelName(String name) {
        return (root, query, cb) ->
                name == null ? null :
                        cb.like(cb.lower(root.get("hotel").get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<FavoriteEntity> withMinRating(BigDecimal minRating) {
        return (root, query, cb) ->
                minRating == null ? null :
                        cb.greaterThanOrEqualTo(root.get("hotel").get("rating"), minRating);
    }

    public static Specification<FavoriteEntity> withMaxRating(BigDecimal maxRating) {
        return (root, query, cb) ->
                maxRating == null ? null :
                        cb.lessThanOrEqualTo(root.get("hotel").get("rating"), maxRating);
    }

    public static Specification<FavoriteEntity> withMinStars(Integer minStars) {
        return (root, query, cb) ->
                minStars == null ? null :
                        cb.greaterThanOrEqualTo(root.get("hotel").get("stars"), minStars);
    }

    public static Specification<FavoriteEntity> withMaxStars(Integer maxStars) {
        return (root, query, cb) ->
                maxStars == null ? null :
                        cb.lessThanOrEqualTo(root.get("hotel").get("stars"), maxStars);
    }

    // Комбинированные спецификации

    public Specification<FavoriteEntity> buildFilterSpecification(
            Integer userId,
            String name,
            BigDecimal minRating,
            BigDecimal maxRating,
            Integer minStars,
            Integer maxStars
    ) {
        return Specification.where(byUserId(userId))
                .and(name != null ? byHotelName(name) : null)
                .and(minRating != null ? byMinRating(minRating) : null)
                .and(maxRating != null ? byMaxRating(maxRating) : null)
                .and(minStars != null ? byMinStars(minStars) : null)
                .and(maxStars != null ? byMaxStars(maxStars) : null);
    }

    public static Specification<FavoriteEntity> byUserId(Integer userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<FavoriteEntity> byHotelName(String name) {
        return (root, query, cb) -> cb.like(
                cb.lower(root.get("hotel").get("name")),
                "%" + name.toLowerCase() + "%"
        );
    }

    public static Specification<FavoriteEntity> byMinRating(BigDecimal minRating) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(
                root.get("hotel").get("rating"),
                minRating
        );
    }

    public static Specification<FavoriteEntity> byMaxRating(BigDecimal maxRating) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(
                root.get("hotel").get("rating"),
                maxRating
        );
    }

    public static Specification<FavoriteEntity> byMinStars(Integer minStars) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(
                root.get("hotel").get("stars"),
                minStars
        );
    }

    public static Specification<FavoriteEntity> byMaxStars(Integer maxStars) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(
                root.get("hotel").get("stars"),
                maxStars
        );
    }

    // Дополнительные спецификации для точечных запросов

    public static Specification<FavoriteEntity> byUserAndHotel(Integer userId, Integer hotelId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("user").get("id"), userId));
            predicates.add(cb.equal(root.get("hotel").get("id"), hotelId));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
