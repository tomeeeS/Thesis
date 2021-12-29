package tmit.bme.telkicar;

import org.junit.jupiter.api.Test;
import tmit.bme.telkicar.base.AbstractTestBase;
import tmit.bme.telkicar.base.TFuvarSearch;
import tmit.bme.telkicar.controller.dto.api.FuvarListItemDTO;
import tmit.bme.telkicar.controller.dto.api.FuvarSearchDTO;

import java.util.List;

public class FuvarSearchHttpTest extends AbstractTestBase {

	//TODO fix TFuvarSearch
	@Test
	public void testFuvarSearch() {
		actAsAdmin();
		List<FuvarListItemDTO> response = new TFuvarSearch(FuvarSearchDTO.builder()
				.destinationAddress("address")
				.destinationLng(2d)
				.build()).send(this);

	}
}
