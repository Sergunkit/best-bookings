package aim.hotel_booking.service;

import aim.hotel_booking.entity.MessageEntity;
import aim.hotel_booking.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.openapitools.model.Message;
import org.openapitools.model.MessageCreateDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public List<Message> getAllMessages() {
        List<MessageEntity> messages = messageRepository.findAllByOrderByCreatedAtAsc();
        return messages.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public List<Message> getUserMessages(Integer userId) {
        List<MessageEntity> messages = messageRepository.findBySenderIdOrReceiverIdOrderByCreatedAtAsc(userId, userId);
        return messages.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public Message createMessage(MessageCreateDto dto) {
        MessageEntity message = new MessageEntity();
        message.setSenderId(dto.getSenderId());
        message.setReceiverId(dto.getReceiverId());
        message.setContent(dto.getContent());
        
        message = messageRepository.save(message);
        Message messageDto = convertToDto(message);
        
        // Отправляем сообщение получателю
        messagingTemplate.convertAndSendToUser(
            dto.getReceiverId().toString(), 
            "/queue/messages", 
            messageDto
        );
    
        // Отправляем копию отправителю
        messagingTemplate.convertAndSendToUser(
            dto.getSenderId().toString(),
            "/queue/messages",
            messageDto
        );
        
        return messageDto;
    }

    private Message convertToDto(MessageEntity entity) {
        Message dto = new Message();
        dto.setId(entity.getId().intValue());
        dto.setSenderId(entity.getSenderId());
        dto.setReceiverId(entity.getReceiverId());
        dto.setContent(entity.getContent());
        dto.setCreatedAt(entity.getCreatedAt().atOffset(ZoneOffset.UTC));
        return dto;
    }
}
