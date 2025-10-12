package aim.hotel_booking.repository;

import aim.hotel_booking.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByCreatedAtAsc(
        Integer senderId, Integer receiverId, Integer receiverId2, Integer senderId2);

    // Получение всех сообщений пользователя (где он отправитель или получатель)
    List<MessageEntity> findBySenderIdOrReceiverIdOrderByCreatedAtAsc(Integer userId, Integer userId2);
    
    // Получение всех сообщений
    List<MessageEntity> findAllByOrderByCreatedAtAsc();
}
