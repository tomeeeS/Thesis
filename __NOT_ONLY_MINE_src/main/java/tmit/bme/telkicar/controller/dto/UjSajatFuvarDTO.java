package tmit.bme.telkicar.controller.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UjSajatFuvarDTO {

	@NotNull
	@NotEmpty
	private String datum;
	@NotNull
	@NotEmpty
	private Integer ora;
	@NotNull
	@NotEmpty
	private Integer perc;
	@NotNull
	@NotEmpty
	private String honnan;
	@NotNull
	@NotEmpty
	private String hova;
	@NotNull
	@NotEmpty
	private String capacity;
	@NotNull
	@NotEmpty
	private String rendszam;
	private String info;
	private String autoLeiras;
	@NotNull
	@NotEmpty
	private Double destinationLat;
	@NotNull
	@NotEmpty
	private Double destinationLng;
	@NotNull
	@NotEmpty
	private String destinationAddress;
	@NotNull
	@NotEmpty
	private String destinationPlaceID;
	@NotNull
	@NotEmpty
	private Double startLat;
	@NotNull
	@NotEmpty
	private Double startLng;
	@NotNull
	@NotEmpty
	private String startAddress;
	@NotNull
	@NotEmpty
	private String startPlaceID;
}