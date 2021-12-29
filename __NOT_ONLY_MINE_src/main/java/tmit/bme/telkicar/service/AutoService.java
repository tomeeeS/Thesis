package tmit.bme.telkicar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tmit.bme.telkicar.controller.dto.UjAutoDTO;
import tmit.bme.telkicar.dao.AutoRepository;
import tmit.bme.telkicar.domain.user.Auto;

@Service
public class AutoService {

	@Autowired
	private AutoRepository autoRepository;

	public Auto saveNewCar(UjAutoDTO ujAuto) {
		Auto auto = Auto.builder()
				.autoLeiras(ujAuto.getAutoLeiras())
				.maxUtasSzam(ujAuto.getMaxUtasSzam())
				.rendszam(ujAuto.getRendszam())
				.szin(ujAuto.getSzin())
				.tipus(ujAuto.getTipus())
				.build();
		return autoRepository.save(auto);
	}

}
