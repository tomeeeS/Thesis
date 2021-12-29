package tmit.bme.telkicar.domain.advert;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import tmit.bme.telkicar.domain.user.Felhasznalo;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "igeny")
public class Igeny extends TravelPlan { // passenger travel plan

    @Column(name = "utasok_szama")
    private Integer utasokSzama; //Jelenleg hirdetővel együtt: Fuvar.getSzabadHely()

    @JsonManagedReference(value="igenyek-hirdeto")
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "hirdeto_id", referencedColumnName = "id")
    private Felhasznalo hirdeto;

    @JsonManagedReference(value="fuvar-igenyek")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "fuvar_id", referencedColumnName = "id")
    private Fuvar fuvar;

    public Integer getUtasokSzama() {
        return utasokSzama;
    }

    @NotNull
    @Override
    public String lpInfo() {
        return "passenger" + id;
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
