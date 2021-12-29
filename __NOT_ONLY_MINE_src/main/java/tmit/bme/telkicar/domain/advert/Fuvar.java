package tmit.bme.telkicar.domain.advert;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import tmit.bme.telkicar.domain.user.Felhasznalo;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.time.LocalDateTime.now;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"szabadHely", "indulasiIdoString", "foglaltHely", "osszesUtas"})
@Table(name = "fuvar")
//@JsonSerialize(using = CustomFuvarSerializer.class)
public class Fuvar extends TravelPlan { // driver travel plan

	@Column(name = "capacity")
	// excluding driver
	private Integer capacity;
	@Column(name = "rendszam")
	private String rendszam;
	@Column(name = "auto_leiras")
	private String autoLeiras;
	@Builder.Default
	@Enumerated(EnumType.STRING)
	private FuvarStatusz statusz = FuvarStatusz.MEGHIRDETVE;

	@Column(name = "meghirdetve_date")
	private LocalDateTime meghirdetveDate;
	@Column(name = "lezarva_date")
	private LocalDateTime lezarvaDate;
	@Column(name = "lemondva_date")
	private LocalDateTime lemondvaDate;
	@Column(name = "teljesitve_date")
	private LocalDateTime teljesitveDate;
	@Column(name = "nem_teljesitve_date")
	private LocalDateTime nemTeljesitveDate;

	//private LocalDateTime cancelDeadline;
	//private LocalDateTime applyDeadline; //?

	@JsonManagedReference(value = "sajatfuvarok-sofor")
	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "sofor_id", referencedColumnName = "id")
	private Felhasznalo sofor;

	// so Kotlin can access it
	public Felhasznalo getSofor() {
		return sofor;
	}

	//TODO RENAME UTAS_FUVAR
	@JsonManagedReference(value = "fuvarok-utasok")
	@ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
	@JoinTable(
			name = "felhasznalo_fuvar",
			joinColumns = {@JoinColumn(name = "fuvar_id")},
			inverseJoinColumns = {@JoinColumn(name = "utas_id")})
	private Set<Felhasznalo> utasok;

	@JsonBackReference(value = "fuvar-igenyek")
	@OneToMany(mappedBy = "fuvar", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Igeny> elvallaltIgenyek;

	@JsonManagedReference(value = "feedback-ride")
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "fuvar", cascade = CascadeType.ALL)
	private List<Feedback> feedbacks;

	public List<Felhasznalo> getOsszesUtas() {
		List<Felhasznalo> osszesUtas;
		if (getUtasok() != null) {
			osszesUtas = new ArrayList<>(getUtasok());
		} else {
			osszesUtas = new ArrayList<>();
		}
		if (elvallaltIgenyek != null) {
			elvallaltIgenyek.forEach(i -> osszesUtas.add(i.getHirdeto()));
		}
		return osszesUtas;
	}

	public Integer getFoglaltHely() {
		return elvallaltIgenyek.stream().mapToInt(Igeny::getUtasokSzama).sum() + utasok.size();
	}

	public Integer getSzabadHely() {
		return capacity - getFoglaltHely();
	}

	public Integer getCapacity() {
		return capacity;
	}

	public String toLogString() {
		return "[" + id + "] " + getFoglaltHely() + "/" + getCapacity() + ": \t" + getSofor().getFullName();
	}

	public void lezar() {
		if (statusz.equals(FuvarStatusz.MEGHIRDETVE)) {
			statusz = FuvarStatusz.LEZARVA;
			lezarvaDate = now();
		} else {
			throw new IllegalStateException();
		}
	}

//	public void veglegesit() {
//		if (statusz.equals(FuvarStatusz.LEZARVA)) {
//			statusz = FuvarStatusz.VEGELEGESITVE;
//		} else {
//			throw new IllegalStateException();
//		}
//	}

	public void lemond() {
		if (statusz.equals(FuvarStatusz.MEGHIRDETVE)
//				|| statusz.equals(FuvarStatusz.VEGELEGESITVE)
				|| statusz.equals(FuvarStatusz.LEZARVA)) {
			statusz = FuvarStatusz.LEMONDVA;
			lemondvaDate = now();
		} else {
			throw new IllegalStateException();
		}
	}

	public void teljesit() {
//		if (statusz.equals(FuvarStatusz.VEGELEGESITVE)) {
		if (statusz.equals(FuvarStatusz.LEZARVA)) {
			statusz = FuvarStatusz.TELJESITVE;
			teljesitveDate = now();
		} else {
			throw new IllegalStateException();
		}
	}

	public void nemTeljesit() {
//		if (statusz.equals(FuvarStatusz.VEGELEGESITVE)) {
		if (statusz.equals(FuvarStatusz.LEZARVA)) {
			statusz = FuvarStatusz.NEM_TELJESITVE;
			nemTeljesitveDate = now();
		} else {
			throw new IllegalStateException();
		}
	}

	@NotNull
	@Override
	public String lpInfo() {
		return "driver" + id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TravelPlan that = (TravelPlan) o;
		return id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
