package aim.hotel_booking.impl;

import aim.hotel_booking.service.AmenityService;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.AmenitiesApiDelegate;
import org.openapitools.model.Amenity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomAmenitiesApiDelegate implements AmenitiesApiDelegate {
    private final AmenityService amenityService;

    @Override
    public ResponseEntity<List<Amenity>> amenitiesList() {
        return amenityService.getAmenities();
    }
}
