package tmit.bme.telkicar.base;

import io.restassured.http.Cookie;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;

public class AbstractTestBase {

	//TODO
	// refactor to static methods
	// frontend date format

	protected final String basePath = "http://localhost:8080/";
	protected Cookie jsessionid;
	private String frontEndDateFormat = "MM/dd/yyyy";

	protected void actAsAdmin() {
		actAs("admin@admin.hu", "a");
	}

	protected void actAs(TFelhasznalo tFelhasznalo) {
		actAs(tFelhasznalo.getEmail(), tFelhasznalo.getPassword());
	}
	protected void actAs(String username, String pw) {
		String response = given()
				.contentType("multipart/form-data")
				.multiPart("username", username)
				.multiPart("password", pw)
				.when()
				.post(basePath + "login")
				.then()
				.statusCode(302)
				.extract().response().cookie("JSESSIONID");

		jsessionid = new Cookie.Builder("JSESSIONID", response).build();
	}

	protected String toFrontEndDateFormat(LocalDateTime date) {
		return  DateTimeFormatter.ofPattern(frontEndDateFormat).format(date);
	}

	protected LocalDateTime today() {
		return LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
	}

	protected LocalDateTime nextMonday() {
		int dayOfWeek = LocalDateTime.now().getDayOfWeek().getValue();
		return today().plusDays(8-dayOfWeek);
	}

	protected LocalDateTime farFutureDate() {
		return LocalDateTime.of(7000, 1, 1, 1, 1);
	}

}
