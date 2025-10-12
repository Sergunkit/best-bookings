package aim.hotel_booking.repository;

import aim.hotel_booking.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends
        JpaRepository<UserEntity, Integer>,
        JpaSpecificationExecutor<UserEntity> {

    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<UserEntity> findByName(String name);
}
