package aim.hotel_booking.service;

import aim.hotel_booking.entity.HotelEntity;
import aim.hotel_booking.mapper.HotelMapper;
import aim.hotel_booking.repository.HotelRepository;
import org.openapitools.model.Hotel;
import org.openapitools.model.HotelCreateDto;
import org.openapitools.model.HotelsList200Response;
import org.openapitools.model.HotelsList200ResponsePagination;
import org.openapitools.model.HotelFilters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.net.URI;

import static aim.hotel_booking.repository.specification.HotelSpecifications.hasNameLike;
import static aim.hotel_booking.repository.specification.HotelSpecifications.hasRatingGreaterThanOrEqual;
import static aim.hotel_booking.repository.specification.HotelSpecifications.hasRatingLessThanOrEqual;
import static aim.hotel_booking.repository.specification.HotelSpecifications.hasStarsGreaterThanOrEqual;
import static aim.hotel_booking.repository.specification.HotelSpecifications.hasStarsLessThanOrEqual;

@Service
public class HotelService {
    private final HotelRepository repository;
    private final HotelMapper mapper;

    public HotelService(HotelRepository repository, HotelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    protected Page<HotelEntity> findAllWithFilters(
            Specification<HotelEntity> spec,
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

    protected HotelEntity save(HotelEntity entity) {
        return repository.save(entity);
    }

    protected HotelEntity findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public ResponseEntity<Hotel> createHotel(HotelCreateDto dto) {
        HotelEntity entity = mapper.toEntity(dto);
        HotelEntity savedEntity = save(entity);
        return ResponseEntity
                .created(URI.create("/hotels/" + savedEntity.getId()))
                .body(mapper.toDto(savedEntity));
    }

    public ResponseEntity<Hotel> getHotel(Integer hotelId) {
        HotelEntity entity = findById(hotelId);
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found");
        }
        return ResponseEntity.ok(mapper.toDto(entity));
    }

    public ResponseEntity<HotelsList200Response> getHotels(
            String name,
            BigDecimal minRating,
            BigDecimal maxRating,
            Integer minStars,
            Integer maxStars,
            String sortBy,
            Sort.Direction sortOrder,
            int page,
            int perPage
    ) {
        Specification<HotelEntity> spec = buildSpecification(name, minRating, maxRating, minStars, maxStars);
        Page<HotelEntity> hotelPage = findAllWithFilters(spec, sortBy, sortOrder, page, perPage);

        HotelsList200Response response = buildResponse(
                hotelPage, name, minRating, maxRating, minStars, maxStars, sortBy, sortOrder.toString());

        return ResponseEntity.ok(response);
    }

    private Specification<HotelEntity> buildSpecification(
            String name, BigDecimal minRating, BigDecimal maxRating, Integer minStars, Integer maxStars) {

        Specification<HotelEntity> spec = Specification.where(null);
        if (name != null) {
            spec = spec.and(hasNameLike(name));
        }
        if (minRating != null) {
            spec = spec.and(hasRatingGreaterThanOrEqual(minRating));
        }
        if (maxRating != null) {
            spec = spec.and(hasRatingLessThanOrEqual(maxRating));
        }
        if (minStars != null) {
            spec = spec.and(hasStarsGreaterThanOrEqual(minStars));
        }
        if (maxStars != null) {
            spec = spec.and(hasStarsLessThanOrEqual(maxStars));
        }
        return spec;
    }

    private HotelsList200Response buildResponse(
            Page<HotelEntity> page, String name, BigDecimal minRating,
            BigDecimal maxRating, Integer minStars, Integer maxStars, String sortBy, String sortOrder) {

        HotelsList200Response response = new HotelsList200Response();
        response.setData(mapper.toDtoList(page.getContent()));

        HotelsList200ResponsePagination pagination = new HotelsList200ResponsePagination();
        pagination.setPage(page.getNumber() + 1);
        pagination.setPerPage(page.getSize());
        pagination.setTotalPages(page.getTotalPages());
        pagination.setTotal((int) page.getTotalElements());
        response.setPagination(pagination);

        response.setSortBy(HotelsList200Response.SortByEnum.fromValue(sortBy));
        response.setSortOrder(HotelsList200Response.SortOrderEnum.fromValue(sortOrder));

        HotelFilters filters = new HotelFilters();
        filters.setName(name);
        filters.setMinRating(minRating);
        filters.setMaxRating(maxRating);
        filters.setMinStars(minStars);
        filters.setMaxStars(maxStars);
        response.setFilters(filters);

        return response;
    }
}
