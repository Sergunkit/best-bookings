package aim.hotel_booking.impl;

import aim.hotel_booking.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.MessagesApiDelegate;
import org.openapitools.model.Message;
import org.openapitools.model.MessageCreateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomMessagesApiDelegate implements MessagesApiDelegate {

    private final MessageService messageService;

    @Override
    public ResponseEntity<List<Message>> messagesList() {
        try {
            return ResponseEntity.ok(messageService.getAllMessages());
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error getting messages", e);
        }
    }

    @Override
    public ResponseEntity<List<Message>> messagesGet(Integer userId) {
        try {
            return ResponseEntity.ok(messageService.getUserMessages(userId));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error getting user messages", e);
        }
    }

    @Override
    public ResponseEntity<Message> messagesCreate(MessageCreateDto messageCreateDto) {
        try {
            return ResponseEntity.status(201).body(messageService.createMessage(messageCreateDto));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating message", e);
        }
    }
}
