package aim.hotel_booking.controller;

import aim.hotel_booking.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.openapitools.model.Message;
import org.openapitools.model.MessageCreateDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public List<Message> getAllMessages() {
        return messageService.getAllMessages();
    }

    @GetMapping("/{userId}")
    public List<Message> getUserMessages(@PathVariable Integer userId) {
        return messageService.getUserMessages(userId);
    }

    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload MessageCreateDto message) {
        messageService.createMessage(message);
    }
}
