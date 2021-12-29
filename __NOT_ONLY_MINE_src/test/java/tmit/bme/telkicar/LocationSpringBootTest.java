package tmit.bme.telkicar;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tmit.bme.telkicar.base.TLocation;
import tmit.bme.telkicar.domain.advert.Location;
import tmit.bme.telkicar.service.LocationService;
import tmit.bme.telkicar.test_data.TestDataBase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

@SpringBootTest
public class LocationSpringBootTest extends TestDataBase {

	@Autowired
	private LocationService locationService;

	@Test
	void testGetDistance() {
		Location a = unwrapTLocation(testLocations.get("bme"));
		Location b = unwrapTLocation(testLocations.get("oktogon"));

		assertThat(locationService.getDistance(a, a), lessThan(1d));
		assertThat(locationService.getDistance(a, b), lessThan(3_600d));
		assertThat(locationService.getDistance(a, b), greaterThan(3_500d));
	}

	private Location unwrapTLocation(TLocation l) {
		return new Location(null, l.lat, l.lng, l.address, l.placeID);
	}
}
