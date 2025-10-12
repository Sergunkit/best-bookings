package aim.hotel_booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
		basePackages = {"org.openapitools", "aim.hotel_booking"},
		excludeFilters = @ComponentScan.Filter(
				type = FilterType.REGEX,
				pattern = {"org.openapitools.api..*", "org.openapitools.configuration..*"}
		)
)
public class HotelBookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotelBookingApplication.class, args);
	}

}
