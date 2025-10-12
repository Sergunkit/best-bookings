package aim.hotel_booking.impl;

import aim.hotel_booking.entity.FavoriteEntity;
import aim.hotel_booking.entity.HotelEntity;
import aim.hotel_booking.entity.UserEntity;
import aim.hotel_booking.repository.FavoriteRepository;
import aim.hotel_booking.repository.HotelRepository;
import aim.hotel_booking.repository.UserRepository;
import aim.hotel_booking.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.FavoritesApiDelegate;
import org.openapitools.model.FavoriteCreateDto;
import org.openapitools.model.Hotel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Component
@RequiredArgsConstructor
public class CustomFavoritesApiDelegate implements FavoritesApiDelegate {

    private final FavoriteService favoriteService;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final FavoriteRepository favoriteRepository;

    @Override
    public ResponseEntity<Hotel> createFavorite(FavoriteCreateDto favoriteCreateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        HotelEntity hotel = hotelRepository.findById(favoriteCreateDto.getHotelId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));
        
        if (favoriteRepository.existsByUserIdAndHotelId(user.getId(), hotel.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Hotel already in favorites");
        }
        
        FavoriteEntity favorite = new FavoriteEntity();
        favorite.setUser(user);
        favorite.setHotel(hotel);
        favoriteRepository.save(favorite);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(hotel));
    }

    @Override
    public ResponseEntity<Void> deleteFavorite(Integer hotelId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        HotelEntity hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));
        
        FavoriteEntity favorite = favoriteRepository.findByUserIdAndHotelId(user.getId(), hotel.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found in favorites"));
        
        favoriteRepository.delete(favorite);
        
        return ResponseEntity.noContent().build();
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
}
