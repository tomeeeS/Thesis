package tmit.bme.telkicar.base;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ResponseBodyExtractionOptions;
import tmit.bme.telkicar.security.RegisterFelhasznaloDTO;

import java.io.IOException;
import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TFelhasznalo {

	private final String createUrl = "user/registration";
	private static final String fuvarraJelentkez = "fuvar/jelentkezes/";
	private static final String listUrl = "sajatfuvarokAPI";
	private static final String listJelentkezesUrl = "sajatjelentkezesekAPI";
	private RegisterFelhasznaloDTO inner;

	public TFelhasznalo(RegisterFelhasznaloDTO obj) {
		inner = obj;
	}

	public TFelhasznalo create(AbstractTestBase base) {
		if (inner.getPassword() != null && inner.getMatchingPassword() == null) {
			inner.setMatchingPassword(inner.getPassword());
		}
		if (inner.getTel() == null) {
			inner.setTel("0000 000 0000");
		}
		if (inner.getEmail() == null) {
			inner.setEmail(new Random().nextInt() + "@telki.test");
		}

		HashMap<String, String> formdata = new HashMap<>();
		formdata.put("firstName", inner.getFirstName());
		formdata.put("lastName", inner.getLastName());
		formdata.put("password", inner.getPassword());
		formdata.put("matchingPassword", inner.getMatchingPassword());
		formdata.put("email", inner.getEmail());
		formdata.put("tel", inner.getTel());

		int statusCode = given()
//				.cookie(base.jsessionid)
				.contentType("application/x-www-form-urlencoded;charset=UTF-8")
				.formParams(formdata)
				.when().post(base.basePath + createUrl)
				.then().extract().response().getStatusCode();

//		assertThat(statusCode, is(200)); //always 200
		return this;
	}

/*
	public static List<Fuvar> getSajatFuvarok(AbstractTestBase base) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		ExtractableResponse<Response> accept = given().cookie(base.jsessionid)
				.header("Accept", ContentType.JSON.getAcceptHeader())
				.when().get(base.basePath + listUrl)
				.then().extract();

		assertThat(accept.statusCode(), is(200));

		ResponseBodyExtractionOptions body = accept.body();
		String jsonString = body.asString();

		try {
			JsonFactory factory = mapper.getFactory();
			JsonParser parser = factory.createParser(jsonString);

			JsonNode actualObj = mapper.readTree(parser);
			List<Fuvar> dtos = new ArrayList<>();
			Iterator<JsonNode> iterator = actualObj.iterator();
			while (iterator.hasNext()) {
				dtos.add(mapper.convertValue(iterator.next(), Fuvar.class));
			}
			return dtos;

//			MappingIterator<Fuvar> fuvarMappingIterator = mapper.readValues(parser, Fuvar.class);
//			return fuvarMappingIterator.readAll();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	*/

	public static List<String> getSajatFuvarokIds(AbstractTestBase base) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		ExtractableResponse<Response> accept = given().cookie(base.jsessionid)
				.header("Accept", ContentType.JSON.getAcceptHeader())
				.when().get(base.basePath + listUrl)
				.then().extract();

		assertThat(accept.statusCode(), is(200));

		ResponseBodyExtractionOptions body = accept.body();
		String jsonString = body.asString();

		try {
			JsonFactory factory = mapper.getFactory();
			JsonParser parser = factory.createParser(jsonString);

			JsonNode actualObj = mapper.readTree(parser);
			List<String> ids = new ArrayList<>();
			Iterator<JsonNode> iterator = actualObj.iterator();
			while (iterator.hasNext()) {
				JsonNode next = iterator.next();
				ids.add(next.get("id").asText());
			}
			return ids;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

/*
	public static List<Fuvar> getJelentkezettFuvarok(AbstractTestBase base) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		ExtractableResponse<Response> accept = given().cookie(base.jsessionid)
				.header("Accept", ContentType.JSON.getAcceptHeader())
				.when().get(base.basePath + listJelentkezesUrl)
				.then().extract();

		assertThat(accept.statusCode(), is(200));

		ResponseBodyExtractionOptions body = accept.body();
		String jsonString = body.asString();

		try {
			JsonFactory factory = mapper.getFactory();
			JsonParser parser = factory.createParser(jsonString);
			JsonNode actualObj = mapper.readTree(parser);

			List<Fuvar> dtos = new ArrayList<>();
			Iterator<JsonNode> iterator = actualObj.iterator();
			while (iterator.hasNext()) {
				dtos.add(mapper.convertValue(iterator.next(), Fuvar.class));
			}
			return dtos;
//			MappingIterator<Fuvar> fuvarMappingIterator = mapper.readValues(parser, Fuvar.class);
//			return fuvarMappingIterator.readAll();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	*/

	public static List<String> getJelentkezettFuvarokIds(AbstractTestBase base) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		ExtractableResponse<Response> accept = given().cookie(base.jsessionid)
				.header("Accept", ContentType.JSON.getAcceptHeader())
				.when().get(base.basePath + listJelentkezesUrl)
				.then().extract();

		assertThat(accept.statusCode(), is(200));

		ResponseBodyExtractionOptions body = accept.body();
		String jsonString = body.asString();

		try {
			JsonFactory factory = mapper.getFactory();
			JsonParser parser = factory.createParser(jsonString);
			JsonNode actualObj = mapper.readTree(parser);

			List<String> ids = new ArrayList<>();
			Iterator<JsonNode> iterator = actualObj.iterator();
			while (iterator.hasNext()) {
				JsonNode next = iterator.next();
				ids.add(next.get("id").asText());
			}
			return ids;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static int attachUtasToFuvar(AbstractTestBase base, Integer fuvarId) {
		return given().cookie(base.jsessionid)
				.when().post(base.basePath + fuvarraJelentkez + fuvarId.toString())
				.then().extract().response().getStatusCode();
	}

	public String getEmail() {
		return inner.getEmail();
	}

	public String getPassword() {
		return inner.getPassword();
	}
}
