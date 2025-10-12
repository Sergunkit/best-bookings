package aim.hotel_booking.service;

import aim.hotel_booking.entity.AmenityEntity;
import aim.hotel_booking.mapper.AmenityMapper;
import aim.hotel_booking.repository.AmenityRepository;
import org.openapitools.model.Amenity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AmenityService {
    private final AmenityRepository repository;
    private final AmenityMapper mapper;

    public AmenityService(AmenityRepository repository, AmenityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public ResponseEntity<List<Amenity>> getAmenities() {
        List<AmenityEntity> entities = repository.findAll();
        List<Amenity> amenities = entities.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(amenities);
    }
}
