package tmit.bme.telkicar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tmit.bme.telkicar.controller.dto.api.FuvarListItemDTO;
import tmit.bme.telkicar.controller.dto.api.FuvarSearchDTO;
import tmit.bme.telkicar.dao.FuvarRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FuvarSearchService {

	@Autowired
	private ServiceHelper helper;
	@Autowired
	private FuvarRepository fuvarRepository;

	private int upperDestinationDistanceLimitMeters = 2_000;
	private int upperStartDistanceLimitMeters = 2_000;

	public List<FuvarListItemDTO> searchByParametersAPI(FuvarSearchDTO fuvarSearch) {

		LocalDate startDate = helper.getDateFromFrontEndPattern(fuvarSearch.getStartDateString());
		LocalDateTime startTime = null;
		if (startDate != null) {
			startTime= startDate.atTime(fuvarSearch.getStartHour(), fuvarSearch.getStartMinute());
		}

		List<FuvarListItemDTO> fuvarListItemDTOS = new ArrayList<>();
		fuvarRepository.search(
				startTime
//				fuvarSearch.getStartLat(),
//				fuvarSearch.getStartLng(),
//				fuvarSearch.getDestinationLat(),
//				fuvarSearch.getDestinationLng(),
//				upperStartDistanceLimitMeters,
//				upperDestinationDistanceLimitMeters
		).forEach(i -> {
			fuvarListItemDTOS.add(new FuvarListItemDTO(i));
		});
		return fuvarListItemDTOS;
	}

}
