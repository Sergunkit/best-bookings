package aim.hotel_booking.impl;

import aim.hotel_booking.service.BookingService;
import aim.hotel_booking.service.UserService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.openapitools.model.UserDto;
import org.openapitools.model.UserCreateDto;
import org.openapitools.model.UserUpdateDto;
import org.openapitools.model.UsersList200Response;
import org.openapitools.model.UserBookingsList200Response;
import org.openapitools.api.UsersApiDelegate;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import aim.hotel_booking.entity.UserEntity;
import aim.hotel_booking.repository.UserRepository;
import aim.hotel_booking.entity.FavoriteEntity;
import aim.hotel_booking.entity.HotelEntity;
import aim.hotel_booking.repository.FavoriteRepository;
import aim.hotel_booking.repository.HotelRepository;
import org.openapitools.model.HotelsList200Response;
import org.openapitools.model.HotelsList200ResponsePagination;
import org.openapitools.model.HotelFilters;
import org.openapitools.model.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import aim.hotel_booking.repository.specification.FavoriteSpecification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
public class CustomUsersApiDelegate implements UsersApiDelegate {
    private final UserService service;
    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final HotelRepository hotelRepository;
    private final FavoriteSpecification favoriteSpecification;

    public CustomUsersApiDelegate(UserService service, BookingService bookingService, UserRepository userRepository,
                                  FavoriteRepository favoriteRepository, HotelRepository hotelRepository,
                                  FavoriteSpecification favoriteSpecification) {
        this.service = service;
        this.bookingService = bookingService;
        this.userRepository = userRepository;
        this.favoriteRepository = favoriteRepository;
        this.hotelRepository = hotelRepository;
        this.favoriteSpecification = favoriteSpecification;
    }

    @Override
    public ResponseEntity<UserDto> usersCreate(UserCreateDto userCreateDto) {
        return service.createUser(userCreateDto);
    }

    @Override
    public ResponseEntity<UsersList200Response> usersList(
            String name,
            String email,
            String sortBy,
            Integer page,
            Integer perPage,
            String sortOrder
    ) {
        // Установка значений по умолчанию
        if (page == null || page < 1) {
            page = 1;
        }
        if (perPage == null || perPage < 1) {
            perPage = 24;
        }
        if (sortBy == null) {
            sortBy = "name";
        }
        if (sortOrder == null) {
            sortOrder = "ASC";
        }

        // Валидация параметров сортировки
        try {
            UsersList200Response.SortByEnum.fromValue(sortBy);
            UsersList200Response.SortOrderEnum.fromValue(sortOrder);
        } catch (IllegalArgumentException e) {
            sortBy = "name";
            sortOrder = "ASC";
        }

        return service.getUsers(
                name,
                email,
                sortBy,
                Sort.Direction.fromString(sortOrder),
                page,
                perPage
        );
    }

    @Override
    public ResponseEntity<UserBookingsList200Response> userBookingsList(
            Integer userId,
            Integer roomId,
            OffsetDateTime checkInBefore,
            OffsetDateTime checkInAfter,
            OffsetDateTime checkOutBefore,
            OffsetDateTime checkOutAfter,
            String sortBy,
            Integer page,
            Integer perPage,
            String sortOrder
    ) {
        try {
            // Валидация параметров
            validatePaginationParams(page, perPage);
            validateSortOrder(sortOrder);
            validateDateParams(checkInBefore, checkInAfter, checkOutBefore, checkOutAfter);

            // Установка значений по умолчанию
            page = (page == null || page < 1) ? 1 : page;
            perPage = (perPage == null || perPage < 1) ? 24 : Math.min(perPage, 100);
            sortBy = (sortBy == null) ? "checkIn" : sortBy;
            sortOrder = (sortOrder == null) ? "ASC" : sortOrder;

            return bookingService.getUserBookings(
                userId,
                roomId,
                checkInBefore,
                checkInAfter,
                checkOutBefore,
                checkOutAfter,
                sortBy,
                Sort.Direction.fromString(sortOrder),
                page,
                perPage
            );
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing request", e);
        }
    }

    @Override
    public ResponseEntity<UserDto> usersGet(Integer userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserDto userDto = new UserDto();
        userDto.setId(userEntity.getId());
        userDto.setName(userEntity.getName());
        userDto.setEmail(userEntity.getEmail());

        return ResponseEntity.ok(userDto);
    }

    @Override
    public ResponseEntity<UserDto> usersUpdate(Integer userId, UserUpdateDto userUpdateDto) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Обновляем только те поля, которые пришли в запросе
        if (userUpdateDto.getName() != null) {
            userEntity.setName(userUpdateDto.getName());
        }
        if (userUpdateDto.getEmail() != null) {
            userEntity.setEmail(userUpdateDto.getEmail());
        }
        if (userUpdateDto.getPassword() != null) {
            userEntity.setPassword(userUpdateDto.getPassword());
        }

        userRepository.save(userEntity);

        UserDto userDto = new UserDto();
        userDto.setId(userEntity.getId());
        userDto.setName(userEntity.getName());
        userDto.setEmail(userEntity.getEmail());

        return ResponseEntity.ok(userDto);
    }

    @Override
    public ResponseEntity<Void> usersDelete(Integer userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        userRepository.delete(userEntity);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<HotelsList200Response> listUserFavorites(
            Integer userId,
            String name,
            BigDecimal minRating,
            BigDecimal maxRating,
            Integer minStars,
            Integer maxStars,
            String sortBy,
            Integer page,
            Integer perPage,
            String sortOrder) {
        
        // Проверяем существование пользователя
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Создаем объект пагинации
        PageRequest pageRequest = PageRequest.of(
                page != null ? page - 1 : 0,
                perPage != null ? perPage : 24,
                Sort.by(Sort.Direction.fromString(sortOrder != null ? sortOrder : "ASC"),
                        "hotel." + (sortBy != null ? sortBy : "name"))
        );

        // Создаем спецификацию для фильтрации
        Specification<FavoriteEntity> spec = favoriteSpecification.buildFilterSpecification(
                userId,
                name,
                minRating,
                maxRating,
                minStars,
                maxStars
        );

        // Получаем избранные отели пользователя с фильтрацией
        Page<FavoriteEntity> favoritesPage = favoriteRepository.findAll(spec, pageRequest);

        // Преобразуем результаты в DTO
        List<Hotel> hotels = favoritesPage.getContent().stream()
                .map(FavoriteEntity::getHotel)
                .map(this::convertToDto)
                .collect(Collectors.toList());

        // Создаем объект ответа
        HotelsList200Response response = new HotelsList200Response();
        response.setData(hotels);
        
        // Создаем и устанавливаем объект пагинации
        HotelsList200ResponsePagination pagination = new HotelsList200ResponsePagination();
        pagination.setPage(page != null ? page : 1);
        pagination.setPerPage(perPage != null ? perPage : 24);
        pagination.setTotal((int) favoritesPage.getTotalElements());
        pagination.setTotalPages(favoritesPage.getTotalPages());
        response.setPagination(pagination);

        // Устанавливаем параметры сортировки и фильтрации
        response.setSortBy(HotelsList200Response.SortByEnum.fromValue(sortBy != null ? sortBy : "name"));
        response.setSortOrder(HotelsList200Response.SortOrderEnum.fromValue(sortOrder != null ? sortOrder : "ASC"));

        // Создаем и устанавливаем объект фильтров
        HotelFilters filters = new HotelFilters();
        filters.setName(name);
        filters.setMinRating(minRating);
        filters.setMaxRating(maxRating);
        filters.setMinStars(minStars);
        filters.setMaxStars(maxStars);
        response.setFilters(filters);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UserDto> userMeGet() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());

        return ResponseEntity.ok(userDto);
    }

    private Hotel convertToDto(HotelEntity entity) {
        Hotel dto = new Hotel();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setStars(entity.getStars());
        dto.setRating(entity.getRating());
        dto.setPhotoSrc(entity.getPhotoSrc());
        return dto;
    }

    private void validatePaginationParams(Integer page, Integer perPage) {
        if (page != null && page < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page number must be positive");
        }
        if (perPage != null && (perPage < 1 || perPage > 100)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Per page must be between 1 and 100");
        }
    }

    private void validateSortOrder(String sortOrder) {
        if (sortOrder != null && !sortOrder.equalsIgnoreCase("ASC") && !sortOrder.equalsIgnoreCase("DESC")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sort order must be either ASC or DESC");
        }
    }

    private void validateDateParams(
            OffsetDateTime checkInBefore,
            OffsetDateTime checkInAfter,
            OffsetDateTime checkOutBefore,
            OffsetDateTime checkOutAfter
    ) {
        if (checkInBefore != null && checkInAfter != null && checkInBefore.isBefore(checkInAfter)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Check-in before date must be after check-in after date");
        }
        if (checkOutBefore != null && checkOutAfter != null && checkOutBefore.isBefore(checkOutAfter)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Check-out before date must be after check-out after date");
        }
    }
}
