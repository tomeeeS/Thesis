package tmit.bme.telkicar.base;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ResponseBodyExtractionOptions;
import tmit.bme.telkicar.controller.dto.api.FuvarListItemDTO;
import tmit.bme.telkicar.controller.dto.api.FuvarSearchDTO;

import java.io.IOException;
import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TFuvarSearch {

	private FuvarSearchDTO inner;

	public TFuvarSearch(FuvarSearchDTO obj) {
		inner = obj;
	}

	public List<FuvarListItemDTO> send(AbstractTestBase base) {
		ObjectMapper mapper = new ObjectMapper();
//		String payload = null;
//		try {
//			payload = mapper.writeValueAsString(inner);
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}

		HashMap<String, String> payload = createAttributeMap(inner);

		ExtractableResponse<Response> accept = given().cookie(base.jsessionid)
				.contentType(ContentType.JSON)
				.queryParams(payload)
				.header("Accept", ContentType.JSON.getAcceptHeader())
				.when().get(base.basePath + "fuvarok/search")
				.then().extract();

		assertThat(accept.statusCode(), is(200));

		ResponseBodyExtractionOptions body = accept.body();
		String jsonString = body.asString();

		try {
			JsonFactory factory = mapper.getFactory();
			JsonParser parser = factory.createParser(jsonString);
			JsonNode actualObj = mapper.readTree(parser);

			List<FuvarListItemDTO> dtos = new ArrayList<>();
			Iterator<JsonNode> iterator = actualObj.iterator();
			while (iterator.hasNext()) {
				dtos.add(mapper.convertValue(iterator.next(), FuvarListItemDTO.class));
			}

			return dtos;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private HashMap<String, String> createAttributeMap(FuvarSearchDTO inner) {
		HashMap<String, String> payload = new HashMap<>();
		if (inner.getStartDateString() != null) payload.put("startDateString", inner.getStartDateString());
		if (inner.getStartHour() != null) payload.put("startHour", String.valueOf(inner.getStartHour()));
		if (inner.getStartMinute() != null) payload.put("startMinute", String.valueOf(inner.getStartMinute()));
		if (inner.getStartLat() != null) payload.put("startLat", String.valueOf(inner.getStartLat()));
		if (inner.getStartLng() != null) payload.put("startLng", String.valueOf(inner.getStartLng()));
		if (inner.getStartAddress() != null) payload.put("startAddress", inner.getStartAddress());
		if (inner.getStartPlaceId() != null) payload.put("startPlaceId", inner.getStartPlaceId());
		if (inner.getDestinationLat() != null) payload.put("destinationLat", String.valueOf(inner.getDestinationLat()));
		if (inner.getDestinationLng() != null) payload.put("destinationLng", String.valueOf(inner.getDestinationLng()));
		if (inner.getDestinationAddress() != null) payload.put("destinationAddress", inner.getDestinationAddress());
		if (inner.getDestinationPlaceId() != null) payload.put("destinationPlaceId", inner.getDestinationPlaceId());

		return payload;
	}
}
