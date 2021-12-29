package tmit.bme.telkicar.controller.dto.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tmit.bme.telkicar.domain.advert.Igeny;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class IgenyListItemDTO {

	private int id;
	private String startAddress;
	private String destinationAddress;
	private int utasokSzama;
	private String info;
	private String indulasiIdoString;

	public IgenyListItemDTO(Igeny igeny) {
		id = igeny.getId();
		startAddress = igeny.getDeparture().getAddress();
		destinationAddress = igeny.getDestination().getAddress();
		utasokSzama = igeny.getUtasokSzama();
		info = igeny.getInfo();
		indulasiIdoString = igeny.getIndulasiIdoString();
	}
}
