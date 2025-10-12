package aim.hotel_booking.impl;

import aim.hotel_booking.service.HotelService;
import aim.hotel_booking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.HotelsApiDelegate;
import org.openapitools.model.Hotel;
import org.openapitools.model.HotelsList200Response;
import org.openapitools.model.HotelRoomsList200Response;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CustomHotelsApiDelegate implements HotelsApiDelegate {

    private final HotelService hotelService;
    private final RoomService roomService;

    @Override
    public ResponseEntity<HotelsList200Response> hotelsList(
            String name,
            BigDecimal minRating,
            BigDecimal maxRating,
            Integer minStars,
            Integer maxStars,
            String sortBy,
            Integer page,
            Integer perPage,
            String sortOrder
    ) {
        try {
            // Валидация параметров
            validatePaginationParams(page, perPage);
            validateSortOrder(sortOrder);
            validateRatingParams(minRating, maxRating);
            validateStarsParams(minStars, maxStars);

            // Установка значений по умолчанию
            page = (page == null || page < 1) ? 1 : page;
            perPage = (perPage == null || perPage < 1) ? 24 : Math.min(perPage, 100);
            sortBy = (sortBy == null) ? "name" : sortBy;
            sortOrder = (sortOrder == null) ? "ASC" : sortOrder;

            return hotelService.getHotels(
                name,
                minRating,
                maxRating,
                minStars,
                maxStars,
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
    public ResponseEntity<HotelRoomsList200Response> hotelRoomsList(
            Integer hotelId,
            String name,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String sortBy,
            Integer page,
            Integer perPage,
            String sortOrder
    ) {
        try {
            // Валидация параметров
            validatePaginationParams(page, perPage);
            validateSortOrder(sortOrder);
            validatePriceParams(minPrice, maxPrice);

            // Установка значений по умолчанию
            page = (page == null || page < 1) ? 1 : page;
            perPage = (perPage == null || perPage < 1) ? 24 : Math.min(perPage, 100);
            sortBy = (sortBy == null) ? "name" : sortBy;
            sortOrder = (sortOrder == null) ? "ASC" : sortOrder;

            return roomService.getRooms(
                hotelId,
                name,
                minPrice,
                maxPrice,
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
    public ResponseEntity<Hotel> hotelsGet(Integer hotelId) {
        try {
            return hotelService.getHotel(hotelId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing request", e);
        }
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

    private void validateRatingParams(BigDecimal minRating, BigDecimal maxRating) {
        if (minRating != null && (minRating.compareTo(BigDecimal.ZERO) < 0 ||
                minRating.compareTo(BigDecimal.valueOf(10.0)) > 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Minimum rating must be between 0 and 10.0");
        }
        if (maxRating != null && (maxRating.compareTo(BigDecimal.ZERO) < 0 ||
                maxRating.compareTo(BigDecimal.valueOf(10.0)) > 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Maximum rating must be between 0 and 10.0");
        }
        if (minRating != null && maxRating != null && minRating.compareTo(maxRating) > 0) {
            throw new ResponseStatusException
                    (HttpStatus.BAD_REQUEST, "Minimum rating cannot be greater than maximum rating");
        }
    }

    private void validatePriceParams(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Minimum price must be positive");
        }
        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Maximum price must be positive");
        }
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new ResponseStatusException
                    (HttpStatus.BAD_REQUEST, "Minimum price cannot be greater than maximum price");
        }
    }

    private void validateStarsParams(Integer minStars, Integer maxStars) {
        if (minStars != null && (minStars < 1 || minStars > 5)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Minimum stars must be between 1 and 5");
        }
        if (maxStars != null && (maxStars < 1 || maxStars > 5)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Maximum stars must be between 1 and 5");
        }
        if (minStars != null && maxStars != null && minStars > maxStars) {
            throw new ResponseStatusException
                    (HttpStatus.BAD_REQUEST, "Minimum stars cannot be greater than maximum stars");
        }
    }
}
