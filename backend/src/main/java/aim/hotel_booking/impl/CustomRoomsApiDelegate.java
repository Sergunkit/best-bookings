package aim.hotel_booking.impl;

import aim.hotel_booking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.RoomsApiDelegate;
import org.openapitools.model.RoomDto;
import org.openapitools.model.RoomAvailableDates;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CustomRoomsApiDelegate implements RoomsApiDelegate {

    private final RoomService roomService;

    @Override
    public ResponseEntity<RoomDto> roomsGet(Integer roomId) {
        try {
            return roomService.roomsGet(roomId);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing request", e);
        }
    }

    @Override
    public ResponseEntity<RoomAvailableDates> roomAvailabilityGet(Integer roomId, LocalDate start, LocalDate end) {
        try {
            return roomService.getRoomAvailability(roomId, start, end);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing request", e);
        }
    }
}
