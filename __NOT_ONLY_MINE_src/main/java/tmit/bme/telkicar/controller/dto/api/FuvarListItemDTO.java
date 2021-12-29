package tmit.bme.telkicar.controller.dto.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tmit.bme.telkicar.domain.advert.Fuvar;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FuvarListItemDTO {

	private int id;
	private String indulasiIdoString;
	private String startAddress;
	private String destinationAddress;
	private String info;
	private String rendszam;
	private String autoLeiras;
	private int capacity;
	private int szabadHely;

	public FuvarListItemDTO(Fuvar fuvar) {
		id=fuvar.getId();
		indulasiIdoString = fuvar.getIndulasiIdoString();
		startAddress = fuvar.getDeparture().getAddress();
		destinationAddress = fuvar.getDestination().getAddress();
		info = fuvar.getInfo();
		rendszam = fuvar.getRendszam();
		autoLeiras = fuvar.getAutoLeiras();
		capacity = fuvar.getCapacity();
		szabadHely = fuvar.getSzabadHely();
	}
}
