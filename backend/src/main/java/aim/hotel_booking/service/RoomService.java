package aim.hotel_booking.service;

import aim.hotel_booking.entity.RoomEntity;
import aim.hotel_booking.entity.BookingEntity;
import aim.hotel_booking.mapper.RoomMapper;
import aim.hotel_booking.repository.HotelRepository;
import aim.hotel_booking.repository.RoomRepository;
import org.openapitools.model.HotelRoomsList200Response;
import org.openapitools.model.RoomCreateDto;
import org.openapitools.model.RoomDto;
import org.openapitools.model.HotelsList200ResponsePagination;
import org.openapitools.model.RoomFilters;
import org.openapitools.model.RoomAvailableDates;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static aim.hotel_booking.repository.specification.RoomSpecifications.hasHotelId;
import static aim.hotel_booking.repository.specification.RoomSpecifications.hasNameLike;
import static aim.hotel_booking.repository.specification.RoomSpecifications.hasPriceGreaterThanOrEqual;
import static aim.hotel_booking.repository.specification.RoomSpecifications.hasPriceLessThanOrEqual;

@Service
public class RoomService {
    private final RoomRepository repository;
    private final HotelRepository hotelRepository;
    private final RoomMapper mapper;
    private final BookingService bookingService;

    public RoomService(RoomRepository repository, HotelRepository hotelRepository, RoomMapper mapper,
                       BookingService bookingService) {
        this.repository = repository;
        this.hotelRepository = hotelRepository;
        this.mapper = mapper;
        this.bookingService = bookingService;
    }

    protected Page<RoomEntity> findAllWithFilters(
            Specification<RoomEntity> spec,
            String sortBy,
            Sort.Direction sortOrder,
            int page,
            int perPage
    ) {
        Pageable pageable = PageRequest.of(page - 1, perPage, Sort.by(sortOrder, sortBy));
        return repository.findAll(spec, pageable);
    }

    protected boolean exists(Integer id) {
        return repository.existsById(id);
    }

    protected RoomEntity save(RoomEntity entity) {
        return repository.save(entity);
    }

    protected RoomEntity findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public ResponseEntity<RoomDto> roomsGet(Integer roomId) {
        RoomEntity entity = findById(roomId);
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found");
        }
        return ResponseEntity.ok(mapper.toDto(entity));
    }

    public ResponseEntity<RoomDto> createRoom(Integer hotelId, RoomCreateDto dto) {
        return hotelRepository.findById(hotelId)
                .map(hotel -> {
                    RoomEntity entity = mapper.toEntity(dto);
                    entity.setHotel(hotel);
                    RoomEntity savedEntity = save(entity);
                    return ResponseEntity
                            .created(URI.create("/hotels/" + hotelId + "/rooms/" + savedEntity.getId()))
                            .body(mapper.toDto(savedEntity));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<HotelRoomsList200Response> getRooms(
            Integer hotelId,
            String name,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String sortBy,
            Sort.Direction sortOrder,
            int page,
            int perPage
    ) {
        if (!hotelRepository.existsById(hotelId)) {
            return ResponseEntity.notFound().build();
        }

        Specification<RoomEntity> spec = buildSpecification(hotelId, name, minPrice, maxPrice);
        Page<RoomEntity> roomPage = findAllWithFilters(spec, sortBy, sortOrder, page, perPage);

        HotelRoomsList200Response response = buildResponse(
                roomPage, name, minPrice, maxPrice, sortBy, sortOrder.toString());

        return ResponseEntity.ok(response);
    }

    private Specification<RoomEntity> buildSpecification(
            Integer hotelId, String name, BigDecimal minPrice, BigDecimal maxPrice) {

        Specification<RoomEntity> spec = Specification.where(hasHotelId(hotelId));
        if (name != null) {
            spec = spec.and(hasNameLike(name));
        }
        if (minPrice != null) {
            spec = spec.and(hasPriceGreaterThanOrEqual(minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and(hasPriceLessThanOrEqual(maxPrice));
        }
        return spec;
    }

    private HotelRoomsList200Response buildResponse(
            Page<RoomEntity> page, String name, BigDecimal minPrice,
            BigDecimal maxPrice, String sortBy, String sortOrder) {

        HotelRoomsList200Response response = new HotelRoomsList200Response();
        response.setData(mapper.toDtoList(page.getContent()));

        HotelsList200ResponsePagination pagination = new HotelsList200ResponsePagination();
        pagination.setPage(page.getNumber() + 1);
        pagination.setPerPage(page.getSize());
        pagination.setTotalPages(page.getTotalPages());
        pagination.setTotal((int) page.getTotalElements());
        response.setPagination(pagination);

        response.setSortBy(HotelRoomsList200Response.SortByEnum.fromValue(sortBy));
        response.setSortOrder(HotelRoomsList200Response.SortOrderEnum.fromValue(sortOrder));

        RoomFilters filters = new RoomFilters();
        filters.setName(name);
        filters.setMinPrice(minPrice);
        filters.setMaxPrice(maxPrice);
        response.setFilters(filters);

        return response;
    }

    public ResponseEntity<RoomAvailableDates> getRoomAvailability(Integer roomId, LocalDate start, LocalDate end) {
        // Проверяем существование номера
        RoomEntity room = findById(roomId);
        if (room == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found");
        }

        // Проверяем валидность дат
        if (start.isAfter(end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before end date");
        }

        // Получаем все бронирования для номера в указанном периоде
        List<BookingEntity> bookings = bookingService.getBookingsForRoom(roomId, start, end);

        // Создаем список всех дат в периоде
        List<LocalDate> allDates = start.datesUntil(end.plusDays(1))
                .collect(Collectors.toList());

        // Фильтруем даты, исключая те, которые заняты
        List<LocalDate> availableDates = allDates.stream()
                .filter(date -> isDateAvailable(date, bookings))
                .collect(Collectors.toList());

        // Создаем и возвращаем ответ
        RoomAvailableDates response = new RoomAvailableDates();
        response.setRoomId(roomId);
        response.setDates(availableDates);
        return ResponseEntity.ok(response);
    }

    private boolean isDateAvailable(LocalDate date, List<BookingEntity> bookings) {
        return bookings.stream()
                .noneMatch(booking -> 
                    !date.isBefore(booking.getCheckInDate().toLocalDate()) && 
                    !date.isAfter(booking.getCheckOutDate().toLocalDate()));
    }
}
