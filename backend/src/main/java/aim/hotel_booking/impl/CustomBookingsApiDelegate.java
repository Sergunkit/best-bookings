package aim.hotel_booking.impl;

import aim.hotel_booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.BookingsApiDelegate;
import org.openapitools.model.Booking;
import org.openapitools.model.BookingCreateDto;
import org.openapitools.model.BookingUpdateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class CustomBookingsApiDelegate implements BookingsApiDelegate {

    private final BookingService bookingService;

    @Override
    public ResponseEntity<Booking> bookingsCreate(BookingCreateDto bookingCreateDto) {
        try {
            validateBookingDates(bookingCreateDto.getCheckIn(), bookingCreateDto.getCheckOut());
            return bookingService.createBooking(bookingCreateDto);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating booking", e);
        }
    }

    @Override
    public ResponseEntity<Void> bookingsDelete(Integer bookingId) {
        try {
            bookingService.deleteBooking(bookingId);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting booking", e);
        }
    }

    @Override
    public ResponseEntity<Booking> bookingsGet(Integer bookingId) {
        try {
            return bookingService.getBooking(bookingId);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error getting booking", e);
        }
    }

    @Override
    public ResponseEntity<Booking> bookingsUpdate(Integer bookingId, BookingUpdateDto bookingUpdateDto) {
        try {
            if (bookingUpdateDto.getCheckIn() != null && bookingUpdateDto.getCheckOut() != null) {
                validateBookingDates(bookingUpdateDto.getCheckIn(), bookingUpdateDto.getCheckOut());
            }
            return bookingService.updateBooking(bookingId, bookingUpdateDto);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating booking", e);
        }
    }

    private void validateBookingDates(OffsetDateTime checkIn, OffsetDateTime checkOut) {
        if (checkIn.isAfter(checkOut)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Check-in date must be before check-out date");
        }
        if (checkIn.isBefore(OffsetDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Check-in date must be in the future");
        }
    }
}
