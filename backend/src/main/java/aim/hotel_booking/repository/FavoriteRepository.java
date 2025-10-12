package aim.hotel_booking.repository;

import aim.hotel_booking.entity.FavoriteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<FavoriteEntity, Integer>,
        JpaSpecificationExecutor<FavoriteEntity> {

    Optional<FavoriteEntity> findByUserIdAndHotelId(Integer userId, Integer hotelId);

    boolean existsByUserIdAndHotelId(Integer userId, Integer hotelId);

    void deleteByUserIdAndHotelId(Integer userId, Integer hotelId);

    // Метод для фильтрации через Specification
    Page<FavoriteEntity> findAll(Specification<FavoriteEntity> spec, Pageable pageable);
}
