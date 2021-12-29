package tmit.bme.telkicar.base;

import tmit.bme.telkicar.controller.dto.UjSajatFuvarDTO;

import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TFuvar {

	private UjSajatFuvarDTO inner;

	public TFuvar(UjSajatFuvarDTO obj) {
		inner = obj;
	}

	public TFuvar create(AbstractTestBase base) {
		HashMap<String, String> formdata1 = new HashMap<>();
		HashMap<String, Integer> formdata2 = new HashMap<>();
		HashMap<String, Double> formdata3 = new HashMap<>();

		formdata1.put("datum", inner.getDatum());
		formdata1.put("capacity", inner.getCapacity());
		formdata1.put("rendszam", inner.getRendszam());
		formdata1.put("info", inner.getInfo());
		formdata1.put("autoLeiras", inner.getAutoLeiras());
		formdata1.put("destinationAddress", inner.getDestinationAddress());
		formdata1.put("destinationPlaceID", inner.getDestinationPlaceID());
		formdata1.put("startAddress", inner.getStartAddress());
		formdata1.put("startPlaceID", inner.getStartPlaceID());

		formdata2.put("ora", inner.getOra());
		formdata2.put("perc", inner.getPerc());

		formdata3.put("destinationLat", inner.getDestinationLat());
		formdata3.put("destinationLng", inner.getDestinationLng());
		formdata3.put("startLat", inner.getStartLat());
		formdata3.put("startLng", inner.getStartLng());

		int statusCode = given().cookie(base.jsessionid)
				.contentType("application/x-www-form-urlencoded;charset=UTF-8")
				.formParams(formdata1)
				.formParams(formdata2)
				.formParams(formdata3)
				.when().post(base.basePath + "sajatfuvar")
				.then().extract().response().getStatusCode();

		assertThat(statusCode, is(200));
		return this;
	}

}
