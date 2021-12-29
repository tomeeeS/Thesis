package tmit.bme.telkicar.domain.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rating")
public class Rating {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@Column(name = "rating")
	private Integer rating;
	@Column(name = "time")
	private LocalDateTime time;

	@JsonBackReference(value="velemenyek-felhasznalo")
	@ManyToOne
	@JoinColumn(name = "felhasznalo_id", referencedColumnName = "id")
	private Felhasznalo felhasznalo;

	//TODO rating giver?
}
