package tmit.bme.telkicar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.view.RedirectView;
import tmit.bme.telkicar.controller.dto.feedback.RideFeedbackDTO;
import tmit.bme.telkicar.controller.dto.UjAutoDTO;
import tmit.bme.telkicar.controller.dto.UjFuvarIgenyDTO;
import tmit.bme.telkicar.controller.dto.UjSajatFuvarDTO;
import tmit.bme.telkicar.domain.user.Felhasznalo;
import tmit.bme.telkicar.service.CreditService;
import tmit.bme.telkicar.service.FelhasznaloService;
import tmit.bme.telkicar.service.FuvarService;
import tmit.bme.telkicar.service.IgenyService;

import java.security.Principal;

@org.springframework.stereotype.Controller
public class Controller {

    @Autowired
    private FelhasznaloService felhasznaloService;
    @Autowired
    private FuvarService fuvarService;
    @Autowired
    private IgenyService igenyService;
    @Autowired
    private CreditService creditService;

    //TODO minden addAtributenál no db-hez nyúlás

    @GetMapping("/")
    public RedirectView home() {
        return new RedirectView("/home");
    }

    @GetMapping("/home")
    public String kezdooldal(Model model, Principal principal) {
        Felhasznalo currentUser = felhasznaloService.getUserByUserName(principal.getName());
        model.addAttribute("fuvarokToBeRatedAsSofor", fuvarService.getFuvarokToBeRatedAsSofor(currentUser.getId()));
        model.addAttribute("fuvarokToBeRatedAsUtas", fuvarService.getFuvarokToBeRatedAsUtas(currentUser.getId()));
        return "home";
    }

    @GetMapping("/osszes-utam")
    public String sajatOldal(Model model, Principal principal) {
        model.addAttribute("sajatFuvarok", fuvarService.getAktivFuvarokByFelhasznalo(principal.getName()));
        model.addAttribute("jelentkezettFuvarok", fuvarService.getUsersAppliedFuvarok(principal.getName()));
        model.addAttribute("fuvarIgenyek", igenyService.getAktivIgenyekByFelhasznalo(principal.getName()));
        Felhasznalo currentUser = felhasznaloService.getUserByUserName(principal.getName());
        model.addAttribute("fuvarokToBeRatedAsSofor", fuvarService.getFuvarokToBeRatedAsSofor(currentUser.getId()));
        model.addAttribute("fuvarokToBeRatedAsUtas", fuvarService.getFuvarokToBeRatedAsUtas(currentUser.getId()));
        return "user-routes";
    }

    @GetMapping("/profil")
    public String sajatProfil(Model model, Principal principal) {
        Felhasznalo currentUser = felhasznaloService.getUserByUserName(principal.getName());
        model.addAttribute("user", currentUser);
        model.addAttribute("sajatFuvarok", fuvarService.getAktivFuvarokByFelhasznalo(currentUser.getId()));
        model.addAttribute("jelentkezettFuvarok", fuvarService.getUsersAppliedFuvarok(principal.getName()));
        model.addAttribute("fuvarIgenyek", igenyService.getAktivIgenyekByFelhasznalo(principal.getName()));
        model.addAttribute("creditHistory", creditService.getCreditEntriesForUser(currentUser.getId()));
        model.addAttribute("fuvarokToBeRatedAsSofor", fuvarService.getFuvarokToBeRatedAsSofor(currentUser.getId()));
        model.addAttribute("fuvarokToBeRatedAsUtas", fuvarService.getFuvarokToBeRatedAsUtas(currentUser.getId()));
        return "user-profile";
    }

    @GetMapping("/user/add-car")
    public String ujAuto(Model model, Principal principal) {
        model.addAttribute("auto", new UjAutoDTO());
        Felhasznalo currentUser = felhasznaloService.getUserByUserName(principal.getName());
        model.addAttribute("fuvarokToBeRatedAsSofor", fuvarService.getFuvarokToBeRatedAsSofor(currentUser.getId()));
        model.addAttribute("fuvarokToBeRatedAsUtas", fuvarService.getFuvarokToBeRatedAsUtas(currentUser.getId()));
        return "user-add-car";
    }

    @GetMapping("/driverADV")
    public String driverADV(Model model, Principal principal) {
        model.addAttribute("fuvar", new UjSajatFuvarDTO());
        Felhasznalo currentUser = felhasznaloService.getUserByUserName(principal.getName());
        model.addAttribute("fuvarokToBeRatedAsSofor", fuvarService.getFuvarokToBeRatedAsSofor(currentUser.getId()));
        model.addAttribute("fuvarokToBeRatedAsUtas", fuvarService.getFuvarokToBeRatedAsUtas(currentUser.getId()));
        return "driver-adv";
    }

    @GetMapping("/passangerADV")
    public String passangerADV(Model model, Principal principal) {
        model.addAttribute("igeny", new UjFuvarIgenyDTO());
        Felhasznalo currentUser = felhasznaloService.getUserByUserName(principal.getName());
        model.addAttribute("fuvarokToBeRatedAsSofor", fuvarService.getFuvarokToBeRatedAsSofor(currentUser.getId()));
        model.addAttribute("fuvarokToBeRatedAsUtas", fuvarService.getFuvarokToBeRatedAsUtas(currentUser.getId()));
        return "passanger-adv";
    }

    @GetMapping("/fuvarok/sajatok")
    public String listFuvarokByCurrentUser(Model model, Principal principal) {
        model.addAttribute("fuvarok", fuvarService.getAktivFuvarokByFelhasznalo(principal.getName()));
        Felhasznalo currentUser = felhasznaloService.getUserByUserName(principal.getName());
        model.addAttribute("fuvarokToBeRatedAsSofor", fuvarService.getFuvarokToBeRatedAsSofor(currentUser.getId()));
        model.addAttribute("fuvarokToBeRatedAsUtas", fuvarService.getFuvarokToBeRatedAsUtas(currentUser.getId()));
        return "my-routes";
    }

    @GetMapping("/fuvarok/jelentkezes")
    public String listFuvarokByOtherUsers(Model model, Principal principal) {
        model.addAttribute("fuvarok", fuvarService.getAktivFuvarokByOthers(principal.getName()));
        Felhasznalo currentUser = felhasznaloService.getUserByUserName(principal.getName());
        model.addAttribute("fuvarokToBeRatedAsSofor", fuvarService.getFuvarokToBeRatedAsSofor(currentUser.getId()));
        model.addAttribute("fuvarokToBeRatedAsUtas", fuvarService.getFuvarokToBeRatedAsUtas(currentUser.getId()));
        return "other-routes";
    }

    @GetMapping("/fuvarok/jelentkezett")
    public String listUsersAppliedFuvar(Model model, Principal principal) {
        model.addAttribute("fuvarok", fuvarService.getUsersAppliedFuvarok(principal.getName()));
        Felhasznalo currentUser = felhasznaloService.getUserByUserName(principal.getName());
        model.addAttribute("fuvarokToBeRatedAsSofor", fuvarService.getFuvarokToBeRatedAsSofor(currentUser.getId()));
        model.addAttribute("fuvarokToBeRatedAsUtas", fuvarService.getFuvarokToBeRatedAsUtas(currentUser.getId()));
        return "applied-routes";
    }

    @GetMapping(path = "/fuvar/{fuvarId}", produces = "application/json")
    public String fuvarDetails(@PathVariable Integer fuvarId, Principal principal, Model model) {
        model.addAttribute("fuvar", fuvarService.getFuvarDetailsById(fuvarId));
        model.addAttribute("iAmDriver", fuvarService.isDriver(fuvarId, principal.getName()));
        model.addAttribute("iAmPassenger", fuvarService.isPassenger(fuvarId, principal.getName()));
        Felhasznalo currentUser = felhasznaloService.getUserByUserName(principal.getName());
        model.addAttribute("fuvarokToBeRatedAsSofor", fuvarService.getFuvarokToBeRatedAsSofor(currentUser.getId()));
        model.addAttribute("fuvarokToBeRatedAsUtas", fuvarService.getFuvarokToBeRatedAsUtas(currentUser.getId()));
        return "details-driver";
    }

    @GetMapping(path = "/hirdetes/{hirdetesId}", produces = "application/json")
    public String hirdetesDetails(@PathVariable Integer hirdetesId, Principal principal, Model model) {
        model.addAttribute("hirdetes", igenyService.getIgenyDetailsById(hirdetesId));
        model.addAttribute("iAmAdvertiser", igenyService.isAdvertiser(hirdetesId, principal.getName()));
        model.addAttribute("iCanGiveALift", felhasznaloService.canGiveALift(hirdetesId, principal.getName()));
        Felhasznalo currentUser = felhasznaloService.getUserByUserName(principal.getName());
        model.addAttribute("fuvarokToBeRatedAsSofor", fuvarService.getFuvarokToBeRatedAsSofor(currentUser.getId()));
        model.addAttribute("fuvarokToBeRatedAsUtas", fuvarService.getFuvarokToBeRatedAsUtas(currentUser.getId()));
        //TODO lemondás
        return "details-passenger";
    }

    @GetMapping(path = "/hirdetes/chose-fuvar/{hirdetesId}", produces = "application/json")
    public String listFuvarsForHirdetes(@PathVariable Integer hirdetesId, Principal principal, Model model) {
        model.addAttribute("hirdetes", igenyService.getIgenyDetailsById(hirdetesId));
        model.addAttribute("fuvarok", fuvarService.getAktivFuvarokByFelhasznalo(principal.getName())); //TODO csak a megfelelő fuvarok: irány / férőhely
        Felhasznalo currentUser = felhasznaloService.getUserByUserName(principal.getName());
        model.addAttribute("fuvarokToBeRatedAsSofor", fuvarService.getFuvarokToBeRatedAsSofor(currentUser.getId()));
        model.addAttribute("fuvarokToBeRatedAsUtas", fuvarService.getFuvarokToBeRatedAsUtas(currentUser.getId()));
        return "hirdetes-chose-fuvar";
    }

    @PostMapping(path = "/hirdetes/{hirdetesId}/attach-fuvar/{fuvarId}", produces = "application/json")
    public RedirectView listFuvarsForHirdetes(@PathVariable Integer hirdetesId, @PathVariable Integer fuvarId, Principal principal, Model model) {
        //TODO valifdation
        // sajat fuvar?
        // van elég hely?
        Felhasznalo currentUser = felhasznaloService.getUserByUserName(principal.getName());
        model.addAttribute("fuvarokToBeRatedAsSofor", fuvarService.getFuvarokToBeRatedAsSofor(currentUser.getId()));
        model.addAttribute("fuvarokToBeRatedAsUtas", fuvarService.getFuvarokToBeRatedAsUtas(currentUser.getId()));
        igenyService.attachFuvar(fuvarId, hirdetesId);
        return new RedirectView("/fuvar/" + fuvarId);
    }

    @GetMapping(path = "/fuvar/feedback/{fuvarId}", produces = "application/json")
    public String fuvarFeedbackPage(@PathVariable Integer fuvarId, Principal principal, Model model) {
        Felhasznalo currentUser = felhasznaloService.getUserByUserName(principal.getName());
        model.addAttribute("fuvarokToBeRatedAsSofor", fuvarService.getFuvarokToBeRatedAsSofor(currentUser.getId()));
        model.addAttribute("fuvarokToBeRatedAsUtas", fuvarService.getFuvarokToBeRatedAsUtas(currentUser.getId()));
        if (fuvarService.isDriver(fuvarId, principal.getName())) {
            model.addAttribute("fuvar", fuvarService.getFuvarDetailsById(fuvarId));
            model.addAttribute("feedback", new RideFeedbackDTO());
            return "feedback-driver";
        } else if (fuvarService.isPassenger(fuvarId, principal.getName())) {
            model.addAttribute("fuvar", fuvarService.getFuvarDetailsById(fuvarId));
            model.addAttribute("feedback", new RideFeedbackDTO());
            return "feedback-passenger";
        } else {
            return "home";
        }
    }

    @PostMapping(path = "/fuvar/driver-feedback/{fuvarId}", produces = "application/json")
    public RedirectView fuvarFeedbackDriver(@PathVariable Integer fuvarId, @RequestBody RideFeedbackDTO feedback, Principal principal, Model model) {
        Felhasznalo currentUser = felhasznaloService.getUserByUserName(principal.getName());
        fuvarService.driverFeedback(fuvarId, currentUser, feedback);
        model.addAttribute("fuvarokToBeRatedAsSofor", fuvarService.getFuvarokToBeRatedAsSofor(currentUser.getId()));
        model.addAttribute("fuvarokToBeRatedAsUtas", fuvarService.getFuvarokToBeRatedAsUtas(currentUser.getId()));
        return new RedirectView("/home");
    }

    @PostMapping(path = "/fuvar/passenger-feedback/{fuvarId}", produces = "application/json")
    public RedirectView fuvarFeedbackPassenger(@PathVariable Integer fuvarId, @RequestBody RideFeedbackDTO feedback, Principal principal, Model model) {
        Felhasznalo currentUser = felhasznaloService.getUserByUserName(principal.getName());
        fuvarService.passengerFeedback(fuvarId, currentUser, feedback);
        model.addAttribute("fuvarokToBeRatedAsSofor", fuvarService.getFuvarokToBeRatedAsSofor(currentUser.getId()));
        model.addAttribute("fuvarokToBeRatedAsUtas", fuvarService.getFuvarokToBeRatedAsUtas(currentUser.getId()));
        return new RedirectView("/home");
    }

    @GetMapping("/telki-budapest")
    public String telkiBudapest(Model model, Principal principal) {
        Felhasznalo currentUser = felhasznaloService.getUserByUserName(principal.getName());
        model.addAttribute("fuvarokToBeRatedAsSofor", fuvarService.getFuvarokToBeRatedAsSofor(currentUser.getId()));
        model.addAttribute("fuvarokToBeRatedAsUtas", fuvarService.getFuvarokToBeRatedAsUtas(currentUser.getId()));
        return "telki-budapest";
    }

    @GetMapping("/budapest-telki")
    public String budapestTelki(Model model, Principal principal) {
        Felhasznalo currentUser = felhasznaloService.getUserByUserName(principal.getName());
        model.addAttribute("fuvarokToBeRatedAsSofor", fuvarService.getFuvarokToBeRatedAsSofor(currentUser.getId()));
        model.addAttribute("fuvarokToBeRatedAsUtas", fuvarService.getFuvarokToBeRatedAsUtas(currentUser.getId()));
        return "budapest-telki";
    }

    @GetMapping("/osszesSofor")
    public String osszesSofor(Model model, Principal principal) {
        Felhasznalo currentUser = felhasznaloService.getUserByUserName(principal.getName());
        model.addAttribute("fuvarokToBeRatedAsSofor", fuvarService.getFuvarokToBeRatedAsSofor(currentUser.getId()));
        model.addAttribute("fuvarokToBeRatedAsUtas", fuvarService.getFuvarokToBeRatedAsUtas(currentUser.getId()));
        return "osszesSofor";
    }

    @GetMapping("/osszesUtas")
    public String osszesUtas(Model model, Principal principal) {
        Felhasznalo currentUser = felhasznaloService.getUserByUserName(principal.getName());
        model.addAttribute("fuvarokToBeRatedAsSofor", fuvarService.getFuvarokToBeRatedAsSofor(currentUser.getId()));
        model.addAttribute("fuvarokToBeRatedAsUtas", fuvarService.getFuvarokToBeRatedAsUtas(currentUser.getId()));
        return "osszesUtas";
    }

    @GetMapping("/allAdvertisement")
    public String allAdvertisement(Model model, Principal principal) {
        Felhasznalo currentUser = felhasznaloService.getUserByUserName(principal.getName());
        model.addAttribute("fuvarokToBeRatedAsSofor", fuvarService.getFuvarokToBeRatedAsSofor(currentUser.getId()));
        model.addAttribute("fuvarokToBeRatedAsUtas", fuvarService.getFuvarokToBeRatedAsUtas(currentUser.getId()));
        return "allAdvertisement";
    }

}
