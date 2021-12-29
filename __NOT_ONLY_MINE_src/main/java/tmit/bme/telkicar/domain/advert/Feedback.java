package tmit.bme.telkicar.domain.advert;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import tmit.bme.telkicar.domain.user.Felhasznalo;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "feedback")
public class Feedback {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@Builder.Default
	@Enumerated(EnumType.STRING)
	private FeedbackRole role = FeedbackRole.PASSENGER;
	@Column(name = "success")
	private Boolean successful;  //TODO unused
	@Column(name = "description")
	private String description;

	@JsonBackReference(value="feedback-ride")
	@ManyToOne
	@JoinColumn(name = "fuvar_id", referencedColumnName = "id")
	private Fuvar fuvar;

	@JsonManagedReference(value="feedback-reviewer")
	@ManyToOne
	@JoinColumn(name = "felhasznalo_id", referencedColumnName = "id")
	private Felhasznalo reviewer;

//	@JsonManagedReference(value = "ride-verifiedPassengers")
	@ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
	@JoinTable(
			name = "passengers_verified_by_driver", //TODO not driver
			joinColumns = {@JoinColumn(name = "feedback_id")},
			inverseJoinColumns = {@JoinColumn(name = "utas_id")})
	private Set<Felhasznalo> verifiedUsers;

//	@JsonManagedReference(value = "ride-notPresentPassengers")
	@ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
	@JoinTable(
			name = "passengers_who_didnt_verify_driver", //TODO not driver
			joinColumns = {@JoinColumn(name = "feedback_id")},
			inverseJoinColumns = {@JoinColumn(name = "utas_id")})
	private Set<Felhasznalo> notVerifiedUsers;
}
