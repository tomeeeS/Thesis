package tmit.bme.telkicar;

import org.junit.jupiter.api.Test;
import tmit.bme.telkicar.base.AbstractTestBase;
import tmit.bme.telkicar.base.TFelhasznalo;
import tmit.bme.telkicar.base.TFuvar;
import tmit.bme.telkicar.controller.dto.UjSajatFuvarDTO;
import tmit.bme.telkicar.security.RegisterFelhasznaloDTO;

import java.util.List;
import java.util.Random;

import static java.lang.Integer.parseInt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static tmit.bme.telkicar.base.TFelhasznalo.getJelentkezettFuvarokIds;
import static tmit.bme.telkicar.base.TFelhasznalo.getSajatFuvarokIds;

public class FelhasznaloHttpTest extends AbstractTestBase {

	@Test
	void testUserAdvAndApply() {
		TFelhasznalo tFelhasznalo = new TFelhasznalo(RegisterFelhasznaloDTO.builder()
				.firstName("test" + new Random().nextInt())
				.lastName("test")
				.password("a")
				.build()).create(this);

		actAs(tFelhasznalo);
		TFuvar tFuvar = new TFuvar(UjSajatFuvarDTO.builder()
				.rendszam("asd-123")
				.capacity("3")
				.autoLeiras("autoleiras")
				.datum("12/12/2020")
				.honnan("Telki")
				.hova("BME")
				.info("info")
				.ora(12)
				.perc(12)
				.build())
				.create(this);
		new TFuvar(UjSajatFuvarDTO.builder()
				.rendszam("asd-123")
				.capacity("3")
				.autoLeiras("autoleiras")
				.datum("12/12/2020")
				.honnan("Telki")
				.hova("BME")
				.info("info")
				.ora(12)
				.perc(12)
				.build())
				.create(this);

		List<String> sajatFuvarok = getSajatFuvarokIds(this);

		actAsAdmin();
		TFelhasznalo.attachUtasToFuvar(this, parseInt(sajatFuvarok.get(0)));
		List<String> jelentkezettFuvarok = getJelentkezettFuvarokIds(this);

		assertThat(jelentkezettFuvarok, hasItem(sajatFuvarok.get(0)));

	}
}
