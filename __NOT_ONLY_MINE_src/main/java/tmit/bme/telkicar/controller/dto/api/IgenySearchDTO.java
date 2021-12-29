package tmit.bme.telkicar.controller.dto.api;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IgenySearchDTO {

	//TODO fuvar | igeny

	LocalDateTime arrivalTime;//TODO
	Double arrivalFelxibility;        //[hour]
	Double maxDistanceAfterParting;   //[m]
	Double destinationLat;
	Double destinationLng;
	String destinationAddress;
	String destinationPlaceId;

	String startDateString;
	@Builder.Default
	Integer startHour = 0;
	@Builder.Default
	Integer startMinute = 0;
	Double timeFlexibilityPercentage;
	Double distanceFlexibilityPercentage;
	//Float startFelxibility;    		//[hour]
	//Float maxDistanceBeforePickUp; 	//[m]
	Double startLat;
	Double startLng;
	String startAddress;
	String startPlaceId;

}
