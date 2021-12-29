package tmit.bme.telkicar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tmit.bme.telkicar.dao.LocationRepository;
import tmit.bme.telkicar.domain.advert.Location;

import static java.lang.StrictMath.*;

@Service
public class LocationService {

	@Autowired
	private LocationRepository locationRepository;

	private final double r = 6371e3;
	private final double rad = PI/180;

	/**
	 * Equirectangular approximation
	 * If performance is an issue and accuracy less important, for small distances
	 * Pythagoras’ theorem can be used on an equi­rectangular projec­tion:
	 * x = Δλ ⋅ cos φm
	 * y = Δφ
	 * d = R ⋅ √x² + y²
	 *
	 * @param a
	 * @param b
	 * @return
	 */

	public double getDistance(Integer a, Integer b) {
		try {
			return getDistance(locationRepository.findById(a).get(), locationRepository.findById(b).get());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1d;
	}

	public double getDistance(Location a, Location b) {
		double lat1 = a.getLat() * rad;
		double lat2 = b.getLat() * rad;
		double lgn1 = a.getLng() * rad;
		double lng2 = b.getLng() * rad;

		double x = (lat1 - lat2) ;
		double y = (lgn1 - lng2) * cos(lat1);
		return sqrt(x * x + y * y) * r;
	}

}

