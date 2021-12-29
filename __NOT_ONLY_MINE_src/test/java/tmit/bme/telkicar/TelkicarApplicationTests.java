package tmit.bme.telkicar;

import org.junit.jupiter.api.Test;
import tmit.bme.telkicar.base.AbstractTestBase;
import tmit.bme.telkicar.base.TFelhasznalo;
import tmit.bme.telkicar.base.TIgeny;
import tmit.bme.telkicar.controller.dto.UjFuvarIgenyDTO;
import tmit.bme.telkicar.security.RegisterFelhasznaloDTO;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Oi! Minden ilyen tesztet ami NEM @SpringBootTest azt localban futó alkalmazás MELLETT lehet indítani
 * Minden teszt metódust:
 * 	org.junit.jupiter.api.Test @Test-el annotálni.
 * 	actAsAdmin(); v. actAs(usr,pw); -vel kezdeni
 * 	minden RestAssured kifejezést given().cookie(jsessionid)-vel kezdeni
 * golden.
 */
//@SpringBootTest
class TelkicarApplicationTests extends AbstractTestBase {

	@Test
	public void contextLoads() {
		when().get(basePath)
				.then().log().all()
				.statusCode(HTTP_OK);
	}

	@Test
	void testAuth() {
		actAsAdmin();
		given().cookie(jsessionid)
				.when().get(basePath + "home")
				.then().log().all()
				.statusCode(HTTP_OK);
	}

	@Test
	void testRegisterUser() {
		new TFelhasznalo(RegisterFelhasznaloDTO.builder()
				.firstName("test")
				.lastName("test")
				.password("a")
				.build())
				.create(this);
	}

//	@Test
//	void testPostRide() {
//		actAsAdmin();
//		new TFuvar(UjSajatFuvarDTO.builder()
//				.rendszam("asd-123")
//				.capacity("3")
//				.autoLeiras("autoleiras")
//				.datum("12/12/2020")
//				.honnan("Telki")
//				.hova("BME")
//				.info("info")
//				.ora(12)
//				.perc(12)
//				.build())
//				.create(this);
//	}

	@Test
	void testNewPassengerAdv() {
		actAsAdmin();
		new TIgeny(UjFuvarIgenyDTO.builder()
				.datum("12/12/2020")
				.honnan("innen")
				.hova("oda")
				.ora(12)
				.perc(12)
				.utasokSzama(2)
				.build())
				.create(this);
	}
}
