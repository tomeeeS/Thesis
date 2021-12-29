package tmit.bme.telkicar.base;

import tmit.bme.telkicar.controller.dto.UjFuvarIgenyDTO;

import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TIgeny {
	private final String url = "fuvar-igeny";
	private UjFuvarIgenyDTO inner;

	public TIgeny(UjFuvarIgenyDTO inner) {
		this.inner = inner;
	}

	public TIgeny create( AbstractTestBase base) {
		HashMap<String, String> formdata = new HashMap<>();
		HashMap<String, Integer> formdata2 = new HashMap<>();

		formdata.put("datum", inner.getDatum());
		formdata.put("honnan", inner.getHonnan());
		formdata.put("hova", inner.getHova());
		formdata.put("info", inner.getInfo());

		formdata2.put("ora", inner.getOra());
		formdata2.put("perc", inner.getPerc());
		formdata2.put("utasokSzama", inner.getUtasokSzama());

		int statusCode = given().cookie(base.jsessionid)
				.contentType("application/x-www-form-urlencoded;charset=UTF-8")
				.formParams(formdata)
				.formParams(formdata2)
				.when().post(base.basePath + url)
				.then().extract().response().getStatusCode();

		assertThat(statusCode, is(200));
		return this;

	}

}
