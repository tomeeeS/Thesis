package tmit.bme.telkicar.domain.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import tmit.bme.telkicar.domain.advert.Feedback;
import tmit.bme.telkicar.domain.advert.Fuvar;
import tmit.bme.telkicar.domain.advert.Igeny;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "felhasznalo")
@JsonIgnoreProperties({"fullName", "password"})
public class Felhasznalo implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@Column(name = "password")
	private String password;
	@Column(name = "enabled")
	private int enabled;
	@Column(name = "email")
	private String email;
	@Column(name = "kereszt_nev")
	private String keresztNev;
	@Column(name = "vezetek_nev")
	private String vezetekNev;
	@Column(name = "tel")
	private String tel;
	@Column(name = "kredit")
	@Builder.Default
	private Integer kredit = 0;

//	@JsonManagedReference
	@OneToMany(mappedBy = "felhasznalo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Role> roles;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_tulaj_id")
	private List<Auto> autok;

	@JsonBackReference(value="sajatfuvarok-sofor")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sofor", cascade = CascadeType.ALL)
	private List<Fuvar> sajatFuvarok;

	@JsonBackReference(value="fuvarok-utasok")
	@ManyToMany(mappedBy = "utasok", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	private List<Fuvar> fuvarok;

	@JsonBackReference(value="igenyek-hirdeto")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "hirdeto", cascade = CascadeType.ALL)
	private List<Igeny> fuvarIgenyek;

	@JsonManagedReference(value="velemenyek-felhasznalo")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "felhasznalo", cascade = CascadeType.ALL)
	private List<Rating> ratings;

	@JsonBackReference(value="feedback-reviewer")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "reviewer", cascade = CascadeType.ALL)
	private List<Feedback> feedbacks;

	@Column(name = "time_flexibility_percentage")
	@Builder.Default
	private Double timeFlexibilityPercentage = 10.0;
	@Column(name = "distance_flexibility_percentage")
	@Builder.Default
	private Double distanceFlexibilityPercentage = 10.0;


	public String getFullName(){
		return vezetekNev + " " + keresztNev;
	}

	public double getRating() {
		return ratings.stream().mapToDouble(Rating::getRating).average().orElse(0d);
	}

	public String getRatingString() {
		return String.format("%.1f", ratings.stream().mapToDouble(Rating::getRating).average().orElse(0d));
	}

	public Double getTimeFlexibilityPercentage() {
		return timeFlexibilityPercentage;
	}

	public Double getDistanceFlexibilityPercentage() {
		return distanceFlexibilityPercentage;
	}

	public void addKredit (int k) {
		kredit += k;
	}
	public void removeKredit (int k) {
		kredit -= k;
	}

	public double getMaxDistanceMultiplier() {
		return 1 + distanceFlexibilityPercentage / 100;
	}

	public double getMaxTimeMultiplier() {
		return 1 + timeFlexibilityPercentage / 100;
	}
}

