package aim.hotel_booking.service;

import aim.hotel_booking.entity.FavoriteEntity;
import aim.hotel_booking.entity.HotelEntity;
import aim.hotel_booking.entity.UserEntity;
import aim.hotel_booking.mapper.FavoriteMapper;
import aim.hotel_booking.repository.FavoriteRepository;
import aim.hotel_booking.repository.HotelRepository;
import aim.hotel_booking.repository.UserRepository;
import aim.hotel_booking.repository.specification.FavoriteSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.openapitools.model.Hotel;
import org.openapitools.model.FavoriteCreateDto;
import org.openapitools.model.HotelsList200Response;
import org.openapitools.model.HotelsList200ResponsePagination;
import org.openapitools.model.HotelFilters;

import java.math.BigDecimal;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final FavoriteMapper favoriteMapper;
    private final FavoriteSpecification favoriteSpecification;

    @Autowired
    public FavoriteService(
            FavoriteRepository favoriteRepository,
            UserRepository userRepository,
            HotelRepository hotelRepository,
            FavoriteMapper favoriteMapper,
            FavoriteSpecification favoriteSpecification
    ) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.hotelRepository = hotelRepository;
        this.favoriteMapper = favoriteMapper;
        this.favoriteSpecification = favoriteSpecification;
    }

    public Hotel createFavorite(FavoriteCreateDto favoriteCreateDto) {
        UserEntity user = getCurrentUser();
        HotelEntity hotel = getHotelById(favoriteCreateDto.getHotelId());

        checkFavoriteNotExists(user.getId(), hotel.getId());

        FavoriteEntity favorite = new FavoriteEntity();
        favorite.setUser(user);
        favorite.setHotel(hotel);
        FavoriteEntity savedFavorite = favoriteRepository.save(favorite);

        return favoriteMapper.toDto(savedFavorite.getHotel());
    }

    public void deleteFavorite(Integer hotelId) {
        UserEntity user = getCurrentUser();
        HotelEntity hotel = getHotelById(hotelId);

        FavoriteEntity favorite = favoriteRepository.findOne(
                        FavoriteSpecification.byUserAndHotel(user.getId(), hotel.getId()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Favorite not found"
                ));

        favoriteRepository.delete(favorite);
    }

    public HotelsList200Response getUserFavorites(
            String name,
            BigDecimal minRating,
            BigDecimal maxRating,
            Integer minStars,
            Integer maxStars,
            Pageable pageable
    ) {
        UserEntity user = getCurrentUser();

        Specification<FavoriteEntity> spec = favoriteSpecification.buildFilterSpecification(
                user.getId(),
                name,
                minRating,
                maxRating,
                minStars,
                maxStars
        );

        Page<FavoriteEntity> favoritesPage = favoriteRepository.findAll(spec, pageable);

        HotelsList200Response response = new HotelsList200Response();
        response.setData(favoritesPage.map(f -> favoriteMapper.toDto(f.getHotel())).toList());
        
        HotelsList200ResponsePagination pagination = new HotelsList200ResponsePagination();
        pagination.setPage(pageable.getPageNumber() + 1);
        pagination.setPerPage(pageable.getPageSize());
        pagination.setTotal((int) favoritesPage.getTotalElements());
        pagination.setTotalPages(favoritesPage.getTotalPages());
        response.setPagination(pagination);

        HotelFilters filters = new HotelFilters();
        filters.setName(name);
        filters.setMinRating(minRating);
        filters.setMaxRating(maxRating);
        filters.setMinStars(minStars);
        filters.setMaxStars(maxStars);
        response.setFilters(filters);

        return response;
    }

    // Вспомогательные методы

    private UserEntity getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByName(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));
    }

    private HotelEntity getHotelById(Integer hotelId) {
        return hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Hotel not found with id: " + hotelId
                ));
    }

    private void checkFavoriteNotExists(Integer userId, Integer hotelId) {
        if (favoriteRepository.exists(FavoriteSpecification.byUserAndHotel(userId, hotelId))) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Hotel is already in favorites"
            );
        }
    }
}
