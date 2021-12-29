package tmit.bme.telkicar.domain.user;

import lombok.*;
import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "auto")
public class Auto {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@Column(name = "rendszam")
	private String rendszam;
	@Column(name = "tipus")
	private String tipus;
	@Column(name = "szin")
	private String szin;
	@Column(name = "utas_max")
	private int maxUtasSzam;
	@Column(name = "auto_leiras")
	private String autoLeiras;

}
