package tmit.bme.telkicar.domain.user;

import lombok.*;
import tmit.bme.telkicar.domain.advert.Fuvar;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "credit_history")
public class CreditEntry {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@Column(name = "value_change")
	private int valueChange;
	@Column(name = "timestamp")
	private LocalDateTime timestamp;

	@ManyToOne(optional = false, cascade = CascadeType.MERGE)
	@JoinColumn(name="user_id")
	private Felhasznalo user;

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name="fuvar_id")
	private Fuvar fuvar;

	public String getTimestampString() {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY.MM.dd HH:mm");
		return timestamp.format(dateTimeFormatter);
	}
}
