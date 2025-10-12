package aim.hotel_booking.service;

import aim.hotel_booking.entity.BookingEntity;
import aim.hotel_booking.mapper.BookingMapper;
import aim.hotel_booking.repository.BookingRepository;
import aim.hotel_booking.repository.specification.BookingSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.openapitools.model.Booking;
import org.openapitools.model.BookingCreateDto;
import org.openapitools.model.BookingUpdateDto;
import org.openapitools.model.UserBookingsList200Response;
import org.openapitools.model.HotelsList200ResponsePagination;
import org.openapitools.model.BookingFilters;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final BookingSpecification bookingSpecification;

    @Autowired
    public BookingService(
        BookingRepository bookingRepository,
        BookingMapper bookingMapper,
        BookingSpecification bookingSpecification
    ) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.bookingSpecification = bookingSpecification;
    }

    public List<BookingEntity> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<BookingEntity> getBookingById(Integer id) {
        return bookingRepository.findById(id);
    }

    public BookingEntity createBooking(BookingEntity booking) {
        return bookingRepository.save(booking);
    }

    public BookingEntity updateBooking(BookingEntity booking) {
        return bookingRepository.save(booking);
    }

    public ResponseEntity<Booking> getBooking(Integer bookingId) {
        return bookingRepository.findById(bookingId)
            .map(bookingMapper::toDto)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Booking not found with id: " + bookingId
            ));
    }

    public ResponseEntity<Void> deleteBooking(Integer bookingId) {
        // Проверяем существование бронирования
        if (!bookingRepository.existsById(bookingId)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Booking not found with id: " + bookingId
            );
        }

        // Удаляем бронирование
        bookingRepository.deleteById(bookingId);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Booking> updateBooking(Integer bookingId, BookingUpdateDto bookingUpdateDto) {
        // Получаем существующее бронирование
        BookingEntity existingBooking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Booking not found with id: " + bookingId
            ));

        // Если обновляются даты, проверяем доступность номера
        if (bookingUpdateDto.getCheckIn() != null || bookingUpdateDto.getCheckOut() != null) {
            OffsetDateTime newCheckIn = bookingUpdateDto.getCheckIn() != null ? 
                bookingUpdateDto.getCheckIn() : existingBooking.getCheckInDate();
            OffsetDateTime newCheckOut = bookingUpdateDto.getCheckOut() != null ? 
                bookingUpdateDto.getCheckOut() : existingBooking.getCheckOutDate();

            // Проверяем пересечение с другими бронированиями
            long overlappingBookings = bookingRepository.count(
                bookingSpecification.datesOverlap(
                    existingBooking.getRoom().getId(),
                    newCheckIn,
                    newCheckOut,
                    bookingId
                )
            );

            if (overlappingBookings > 0) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Room is not available for the selected dates"
                );
            }
        }

        // Обновляем поля бронирования
        if (bookingUpdateDto.getUserId() != null) {
            existingBooking.getUser().setId(bookingUpdateDto.getUserId());
        }
        if (bookingUpdateDto.getRoomId() != null) {
            existingBooking.getRoom().setId(bookingUpdateDto.getRoomId());
        }
        if (bookingUpdateDto.getCheckIn() != null) {
            existingBooking.setCheckInDate(bookingUpdateDto.getCheckIn());
        }
        if (bookingUpdateDto.getCheckOut() != null) {
            existingBooking.setCheckOutDate(bookingUpdateDto.getCheckOut());
        }

        // Сохраняем обновленное бронирование
        BookingEntity updatedBooking = bookingRepository.save(existingBooking);
        return ResponseEntity.ok(bookingMapper.toDto(updatedBooking));
    }

    public ResponseEntity<Booking> createBooking(BookingCreateDto bookingCreateDto) {
        // Проверяем доступность номера
        long overlappingBookings = bookingRepository.count(
            bookingSpecification.datesOverlap(
                bookingCreateDto.getRoomId(),
                bookingCreateDto.getCheckIn(),
                bookingCreateDto.getCheckOut(),
                null
            )
        );

        if (overlappingBookings > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room is not available for the selected dates");
        }

        // Создаем новое бронирование
        BookingEntity bookingEntity = bookingMapper.toEntity(bookingCreateDto);
        BookingEntity savedBooking = bookingRepository.save(bookingEntity);
        Booking booking = bookingMapper.toDto(savedBooking);

        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    public ResponseEntity<UserBookingsList200Response> getUserBookings(
        Integer userId,
        Integer roomId,
        OffsetDateTime checkInBefore,
        OffsetDateTime checkInAfter,
        OffsetDateTime checkOutBefore,
        OffsetDateTime checkOutAfter,
        String sortBy,
        Sort.Direction sortOrder,
        Integer page,
        Integer perPage
    ) {
        try {
            System.out.println("Starting getUserBookings with params: userId=" + userId + ", roomId=" + roomId);
            
            // Создаем спецификацию для фильтрации
            Specification<BookingEntity> spec = Specification.where(bookingSpecification.hasUserId(userId))
                .and(bookingSpecification.hasRoomId(roomId))
                .and(bookingSpecification.checkInBefore(checkInBefore))
                .and(bookingSpecification.checkInAfter(checkInAfter))
                .and(bookingSpecification.checkOutBefore(checkOutBefore))
                .and(bookingSpecification.checkOutAfter(checkOutAfter));

            System.out.println("Specification created");

            // Создаем объект пагинации
            String sortField = sortBy.equals("checkIn") ? "checkInDate" : sortBy;
            Pageable pageable = PageRequest.of(page - 1, perPage, sortOrder, sortField);
            System.out.println("Pageable created");

            // Получаем страницу бронирований
            Page<BookingEntity> bookingsPage = bookingRepository.findAll(spec, pageable);
            System.out.println("Bookings page retrieved, total elements: " + bookingsPage.getTotalElements());

            // Создаем ответ
            UserBookingsList200Response response = new UserBookingsList200Response();
            System.out.println("Response object created");

            response.setData(bookingsPage.getContent().stream()
                .map(bookingMapper::toDto)
                .toList());
            System.out.println("Data set in response");

            // Устанавливаем пагинацию
            HotelsList200ResponsePagination pagination = new HotelsList200ResponsePagination();
            pagination.setPage(page);
            pagination.setPerPage(perPage);
            pagination.setTotalPages(bookingsPage.getTotalPages());
            pagination.setTotal((int) bookingsPage.getTotalElements());
            response.setPagination(pagination);
            System.out.println("Pagination set in response");

            // Устанавливаем фильтры
            BookingFilters filters = new BookingFilters();
            filters.setUserId(userId);
            filters.setRoomId(roomId);
            filters.setCheckInBefore(checkInBefore);
            filters.setCheckInAfter(checkInAfter);
            filters.setCheckOutBefore(checkOutBefore);
            filters.setCheckOutAfter(checkOutAfter);
            response.setFilters(filters);
            System.out.println("Filters set in response");

            // Устанавливаем сортировку
            response.setSortBy(UserBookingsList200Response.SortByEnum.fromValue(sortBy));
            response.setSortOrder(UserBookingsList200Response.SortOrderEnum.fromValue(sortOrder.name()));
            System.out.println("Sorting set in response");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Error in getUserBookings: " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing request", e);
        }
    }

    public List<BookingEntity> getBookingsForRoom(Integer roomId, LocalDate start, LocalDate end) {
        Specification<BookingEntity> spec = Specification.where(bookingSpecification.hasRoomId(roomId))
            .and(bookingSpecification.checkInBefore(end.atStartOfDay().atOffset(ZoneOffset.UTC)))
            .and(bookingSpecification.checkOutAfter(start.atStartOfDay().atOffset(ZoneOffset.UTC)));
        
        return bookingRepository.findAll(spec);
    }
}
