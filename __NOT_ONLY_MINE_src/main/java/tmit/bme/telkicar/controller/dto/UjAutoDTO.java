package tmit.bme.telkicar.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UjAutoDTO {

	private String rendszam;
	private String tipus;
	private String szin;
	private int maxUtasSzam;
	private String autoLeiras; //TODO kell ez?
}
