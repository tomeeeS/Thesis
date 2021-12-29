package tmit.bme.telkicar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import tmit.bme.telkicar.controller.dto.UjAutoDTO;
import tmit.bme.telkicar.controller.dto.UjFuvarIgenyDTO;
import tmit.bme.telkicar.controller.dto.UjSajatFuvarDTO;
import tmit.bme.telkicar.controller.dto.api.FuvarListItemDTO;
import tmit.bme.telkicar.controller.dto.api.FuvarSearchDTO;
import tmit.bme.telkicar.controller.dto.api.IgenyListItemDTO;
import tmit.bme.telkicar.controller.dto.api.IgenySearchDTO;
import tmit.bme.telkicar.dao.FelhasznaloRepository;
import tmit.bme.telkicar.domain.user.Auto;
import tmit.bme.telkicar.domain.advert.Fuvar;
import tmit.bme.telkicar.service.*;

import java.security.Principal;
import java.util.List;

@RestController
public class APIController {

	@Autowired
	private FelhasznaloRepository felhasznaloRepo;
	@Autowired
	private FuvarService fuvarService;
	@Autowired
	private IgenyService igenyService;
	@Autowired
	private AutoService autoService;
	@Autowired
	private FelhasznaloService felhasznaloService;
	@Autowired
	private FuvarSearchService fuvarSearchService;

	//TODO delete
	@GetMapping("/felhasznalok")
	public List<Object> listFelhasznalok() {
		return felhasznaloRepo.listFelhasznalok();
	}

	@PostMapping(path = "/sajatfuvar")
	public ModelAndView newFuvarAsSofor(UjSajatFuvarDTO ujFuvar, Principal principal) {
		//TODO error messages
		// frontend message handling
		if (fuvarService.isValid(ujFuvar)) {
			fuvarService.registerNewFuvar(ujFuvar, principal.getName());
			return new ModelAndView("my-routes")
					.addObject("fuvarok", fuvarService.getAktivFuvarokByFelhasznalo(principal.getName()));
//			return new RedirectView("/fuvar/" + id); //TODO
		} else {
			ujFuvar.setDatum(null);
			return new ModelAndView("driver-adv")
					.addObject("fuvar", ujFuvar)
					.addObject("error", true);
		}
	}

	@PostMapping(path = "/fuvar-igeny")
	public ModelAndView newIgenyAsPassenger(UjFuvarIgenyDTO ujIgeny, Principal principal) {
		//TODO validation
		igenyService.registerNewIgeny(ujIgeny, principal.getName());
		return new ModelAndView("home");
	}

	@PostMapping(path = "/user/add-car")
	public RedirectView newCar(UjAutoDTO ujAuto, Principal principal) {
		//TODO validation
		Auto auto = autoService.saveNewCar(ujAuto);
		felhasznaloService.addCar(principal.getName(), auto);
		return new RedirectView("/profil");
	}

	@PostMapping(path = "fuvar/jelentkezes/{fuvarId}")
	public RedirectView applyForFuvar(@PathVariable Integer fuvarId, Principal principal, RedirectAttributes redirectAttributes) {
		try {
			fuvarService.addPassenger(fuvarId, principal.getName());
		} catch (NoCapacityException e) { //TODO ne urlben
			RedirectView redirectView = new RedirectView("/fuvarok/jelentkezes");
			redirectView.addStaticAttribute("error", "A jelentkezés pillanatában nem volt férőhely.");
			return redirectView;
		}
		return new RedirectView("/home"); //TODO sajat fuvarok?
	}

	@GetMapping(path = "fuvar/lejelentkezes/{fuvarId}")
	public RedirectView detachFromFuvar(@PathVariable Integer fuvarId, Principal principal, RedirectAttributes redirectAttributes) {
		try {
			fuvarService.deletePassenger(fuvarId, principal.getName());
		} catch (NoCapacityException e) { //TODO ne urlben
			RedirectView redirectView = new RedirectView("/fuvarok/jelentkezett");
			redirectView.addStaticAttribute("error", "Nem sikerült lejelentkezni.");
			return redirectView;
		}
		return new RedirectView("/osszes-utam");
	}

	@GetMapping(path = "fuvar/lemondas/{fuvarId}")
	public RedirectView deleteFuvar(@PathVariable Integer fuvarId, Principal principal, RedirectAttributes redirectAttributes) {
		fuvarService.lemondFuvar(fuvarId, principal.getName());
		return new RedirectView("/osszes-utam");
	}

	@GetMapping(path = "hirdetes/lemondas/{hirdetesId}")
	public RedirectView deleteIgeny(@PathVariable Integer hirdetesId, Principal principal, RedirectAttributes redirectAttributes) {
		igenyService.deleteIgeny(hirdetesId, principal.getName());
		return new RedirectView("/osszes-utam");
	}

	/**
	 * DEPRECATED
	 * use fuvarAPIList
	 *
	 * @param date        format has to be mm/dd/yyyy
	 * @param destination either "telki-budapest" or "budapest-telki"
	 * @return Sima Fuvar lista, kézzel kiszedett password mezőkkel...
	 */
	@Deprecated
	@GetMapping(path = "/fuvarok/datum-uticel", produces = "application/json")
	public List<FuvarListItemDTO> fuvarList(@RequestParam(value = "date") String date, @RequestParam(value = "destination") String destination) {
		return fuvarService.listByParametersAPIold(date, destination);
	}
	@Deprecated
	@GetMapping(path = "/fuvarok/uticel", produces = "application/json")
	public List<FuvarListItemDTO> fuvarList(@RequestParam(value = "destination") String destination) {
		return fuvarService.listByParametersAPIold(destination);
	}

//	/**
//	 * Meghirdetett Fuvarokat ad vissza a paraméterek alapján.
//	 * Mindkét paraméter benne kell legyen a requestben, de amelyik üres, aszerint nem szűr.
//	 * <p>
//	 * valid kérések pl:
//	 * localhost:8080/fuvarok/filter?date=04/05/2020&destination=telki-budapest
//	 * localhost:8080/fuvarok/filter?date=&destination=
//	 * <p>
//	 * Válasz formája:
//	 * <pre>
//	 * [
//	 *     {
//	 *         "id": 35,
//	 *         "indulasiIdoString": "2020.04.05 13:12",
//	 *         "indulasiHely": "Telki",
//	 *         "uticel": "budapest",
//	 *         "info": "Nem állunk meg senki kedvéért",
//	 *         "rendszam": "dbd-123", //TODO lehet h nem kéne
//	 *         "autoLeiras": "kék",
//	 *         "capacity": 4,
//	 *         "szabadHely": 4
//	 *     },
//	 *     {
//	 *         ...
//	 *     }
//	 * ]
//	 * </pre>
//	 *
//	 * @param date format has to be mm/dd/yyyy
//	 * @param destination either "telki-budapest" or "budapest-telki"
//	 * @return JSON list of FuvarListItemDTO
//	 */
//	@GetMapping(path = "/fuvarok/filter", produces = "application/json")
//	public List<FuvarListItemDTO> fuvarAPIList(@RequestParam(value = "date") String date, @RequestParam(value = "destination") String destination) {
//		return fuvarService.listByParametersAPI(date, destination);
//	}

	@GetMapping(path = "/fuvarok/search", produces = "application/json")
	public List<FuvarListItemDTO> fuvarAPISearch(FuvarSearchDTO fuvarSearch) {
		return fuvarSearchService.searchByParametersAPI(fuvarSearch);
	}

	@GetMapping(path = "/igenyek/filter", produces = "application/json")
	public List<IgenyListItemDTO> igenyAPIList(IgenySearchDTO igenySearch) {
		return igenyService.searchByParametersAPI(igenySearch);
	}

	//TODO DELETE / used for tests
	@GetMapping(path = "/sajatfuvarokAPI", produces = "application/json")
	public List<Fuvar> listFuvarokByCurrentUser(Principal principal) {
		return fuvarService.getAktivFuvarokByFelhasznalo(principal.getName());
	}

	@GetMapping("/sajatjelentkezesekAPI")
	public List<Fuvar> listUsersAppliedFuvar( Principal principal) {
		return fuvarService.getUsersAppliedFuvarok(principal.getName());
	}

}
