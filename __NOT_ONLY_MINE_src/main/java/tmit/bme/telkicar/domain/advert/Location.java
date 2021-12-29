package tmit.bme.telkicar.domain.advert;

import lombok.*;
import tmit.bme.telkicar.logic.geography.GeoPoint;
import tmit.bme.telkicar.logic.helpers.AppContextHelper;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "location")
public class Location {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "lat")
	private double lat;
	@Column(name = "lng")
	private double lng;
	@Column(name = "address")
	private String address;
	@Column(name = "place_id")
	private String placeId;

	public Boolean isTheSpecialLocation(AppContextHelper appContextHelper) {
		return lat >= appContextHelper.getSpecialLatitudeMinBound() &&
			lat < appContextHelper.getSpecialLatitudeMaxBound() &&
			lng > appContextHelper.getSpecialLongitudeMinBound() &&
			lng < appContextHelper.getSpecialLongitudeMaxBound();
	}

	public GeoPoint toGeoPoint() {
		return new GeoPoint(lat, lng);
	}

}
