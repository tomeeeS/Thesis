package tmit.bme.telkicar.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tmit.bme.telkicar.controller.dto.UjSajatFuvarDTO;
import tmit.bme.telkicar.controller.dto.api.FuvarListItemDTO;
import tmit.bme.telkicar.controller.dto.feedback.RideFeedbackDTO;
import tmit.bme.telkicar.controller.dto.feedback.UserFeedbackDTO;
import tmit.bme.telkicar.dao.*;
import tmit.bme.telkicar.domain.advert.*;
import tmit.bme.telkicar.domain.user.Felhasznalo;
import tmit.bme.telkicar.domain.user.Rating;
import tmit.bme.telkicar.logic.geography.roadnetwork.RoadNetwork;
import tmit.bme.telkicar.logic.geography.roadnetwork.RoadNode;
import tmit.bme.telkicar.logic.helpers.AppContextHelper;
import tmit.bme.telkicar.notification.EmailService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;
import static tmit.bme.telkicar.domain.advert.FeedbackRole.DRIVER;
import static tmit.bme.telkicar.domain.advert.FeedbackRole.PASSENGER;

@Service
public class FuvarService {

    @Autowired
    private FuvarRepository fuvarRepository;
    @Autowired
    private FelhasznaloRepository felhasznaloRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ServiceHelper helper;
    @Autowired
    private IgenyService igenyService;
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private CreditService creditService;
    @Autowired
    private RoadNetwork roadNetwork;
    @Autowired
    private AppContextHelper appContextHelper;

    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Scheduled(fixedRate = 60_000)
    public void expiredFuvarStateJob() {
        List<Fuvar> withElapsedIndulasiIdo = fuvarRepository.getWithElapsedIndulasiIdo(now().toString());
        Boolean hasLogMessage = false;
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\nLezart fuvarok: ");
        for (Fuvar fuvar : withElapsedIndulasiIdo) {
            if (fuvar.getStatusz().equals(FuvarStatusz.MEGHIRDETVE)) {
                fuvar.lezar();
                hasLogMessage = true;
                logMessage.append("\n" + fuvar.toLogString());

                if (fuvar.getOsszesUtas().size() < 1) {
                    fuvar.teljesit();
                    logMessage.append(" \t(teljesitve)");
                }

                fuvarRepository.save(fuvar);
            }
        }
        if (hasLogMessage) {
            logger.info(logMessage.toString());
        }
        //TODO same for Igeny
    }

    @Scheduled(fixedRate = 1_000 * 60 * 60)
    public void expiredFuvarFeedbackJob() {
        List<Fuvar> withExpiredFeedbackTime = fuvarRepository.getWithElapsedFeedbackTime(now().minusDays(2).toString());
        //nincs olyan amire mindenki adott feedbacket - ezek azok ahol timeout miatt nem várjuk meg mindet

        StringBuilder logMessage = new StringBuilder();
        logMessage.append("\nFeedback expired for rides: ");

        for (Fuvar fuvar : withExpiredFeedbackTime) {
            int passengerCount = fuvar.getUtasok().size(); //TODO + Igenyek.count()
            Optional<Feedback> driverFeedbackOptional = fuvar.getFeedbacks().stream().filter(f -> f.getRole().equals(DRIVER)).findFirst();
            boolean hasDriverFeedback = driverFeedbackOptional.isPresent();
            boolean hasPassengerFeedback = fuvar.getFeedbacks().stream().anyMatch(f -> f.getRole().equals(PASSENGER));
            boolean hasPositivePassengerFeedback = fuvar.getFeedbacks().stream()
                .filter(f -> f.getRole().equals(PASSENGER))
                .anyMatch(feedback -> !feedback.getVerifiedUsers().isEmpty());

            //nem telj
            //	driver: utas no && 1 utas (-)
            //	driver: - 		&& all utas no

            if (passengerCount == 1 && !hasPassengerFeedback && hasDriverFeedback && driverFeedbackOptional.get().getVerifiedUsers().isEmpty()
                || (!hasDriverFeedback && !hasPositivePassengerFeedback)) {
                //nem telj
                fuvar.nemTeljesit();
                fuvarRepository.save(fuvar);
                logger.info("Fuvar nem teljesitve [" + fuvar.getId() + "]");
            } else {
                //telj
                fuvar.teljesit();
                fuvarRepository.save(fuvar);
                logger.info("Fuvar teljesitve [" + fuvar.getId() + "]");
            }

            creditService.distributeUserCreditsWithIncompleteFeedbacks(fuvar);
        }

        if (!withExpiredFeedbackTime.isEmpty()) {
            logger.info(logMessage.toString());
        }

    }


    public void driverFeedback(Integer fuvarId, Felhasznalo user, RideFeedbackDTO feedback) {
        //TODO check if driver
        //TODO check if not already exists
        Fuvar fuvar = fuvarRepository.getFuvarById(fuvarId);
        Feedback.FeedbackBuilder driverFeedbackBuilder = Feedback.builder()
            .successful(feedback.getCompleted())
            .description("")
            .role(DRIVER)
            .reviewer(user)
            .fuvar(fuvar);

        HashSet<Felhasznalo> verifiedSet = new HashSet<>();
        HashSet<Felhasznalo> notVerifiedSet = new HashSet<>();
        for (UserFeedbackDTO ufb : feedback.getUserFeedbacks()) {
            Felhasznalo ratedUser = felhasznaloRepository.findById(ufb.getUserId()).get();
            //TODO check if utas
            if (ufb.getWasPresent()) {
                verifiedSet.add(ratedUser);
            } else {
                notVerifiedSet.add(ratedUser);
            }

            Rating passengerRating = Rating.builder()
                .felhasznalo(ratedUser)
                .rating(ufb.getRating())
                .time(now())
                .build();
            ratingRepository.save(passengerRating);
        }
        Feedback driverFeedback = driverFeedbackBuilder
            .verifiedUsers(verifiedSet)
            .notVerifiedUsers(notVerifiedSet)
            .build();
        feedbackRepository.save(driverFeedback);
        fuvar.getFeedbacks().add(driverFeedback);

        checkForAllFeedbacksPresent(fuvar);
    }


    public void passengerFeedback(Integer fuvarId, Felhasznalo user, RideFeedbackDTO feedback) {
        //TODO check if utas
        //TODO check if not already exists
        Fuvar fuvarById = fuvarRepository.getFuvarById(fuvarId);
        Feedback.FeedbackBuilder passengerFeedbackBuilder = Feedback.builder()
            .successful(feedback.getCompleted())
            .description("")
            .role(PASSENGER)
            .reviewer(user)
            .fuvar(fuvarById);

        UserFeedbackDTO userFeedbackDTO = feedback.getUserFeedbacks().get(0);
        Felhasznalo driver = felhasznaloRepository.findById(userFeedbackDTO.getUserId()).get();

        HashSet<Felhasznalo> driverSet = new HashSet<>();
        driverSet.add(driver);
        if (userFeedbackDTO.getWasPresent()) {
            passengerFeedbackBuilder.verifiedUsers(driverSet);
        } else {
            passengerFeedbackBuilder.notVerifiedUsers(driverSet);
        }
        Feedback passengerFeedback = passengerFeedbackBuilder.build();
        feedbackRepository.save(passengerFeedback);
        fuvarById.getFeedbacks().add(passengerFeedback);

        Rating passengerRating = Rating.builder()
            .felhasznalo(driver)
            .rating(userFeedbackDTO.getRating())
            .time(now())
            .build();
        ratingRepository.save(passengerRating);

        checkForAllFeedbacksPresent(fuvarById);
    }


    public void checkForAllFeedbacksPresent(Fuvar fuvar) {
        HashSet<Integer> people = fuvar.getUtasok().stream()
            .map(Felhasznalo::getId)
            .collect(Collectors.toCollection(HashSet::new));
        people.addAll(Collections.singletonList(fuvar.getSofor().getId()));

        HashSet<Integer> reviewers = fuvar.getFeedbacks().stream()
            .map(Feedback::getReviewer)
            .map(Felhasznalo::getId)
            .collect(Collectors.toCollection(HashSet::new));

        Optional<Feedback> driverFeedback = fuvar.getFeedbacks().stream().filter(f -> f.getRole().equals(DRIVER)).findFirst();
        if (driverFeedback.isPresent() && reviewers.containsAll(people)) {
            List<Feedback> passengerFeedbacks = fuvar.getFeedbacks().stream().filter(f -> f.getRole().equals(PASSENGER)).collect(toList());
            Boolean hasVerifiedDriverInPassengerFeedbacks = passengerFeedbacks.stream()
                .map(Feedback::getVerifiedUsers)
                .flatMap(Collection::stream)
                .anyMatch(user -> user.getId().equals(fuvar.getSofor().getId()));

            if (!driverFeedback.get().getVerifiedUsers().isEmpty() && hasVerifiedDriverInPassengerFeedbacks) {
                fuvar.teljesit();
                fuvarRepository.save(fuvar);
                logger.info("Fuvar teljesitve [" + fuvar.getId() + "]");
            } else {
                fuvar.nemTeljesit();
                fuvarRepository.save(fuvar);
                logger.info("Fuvar nem teljesitve [" + fuvar.getId() + "]");
            }
            creditService.distributeUserCredits(fuvar);

        }

    }

    public List<Fuvar> getFuvarokToBeRatedAsSofor(Integer soforId) {
        return fuvarRepository.getFuvarokToBeRatedAsSofor(soforId);
    }

    public List<Fuvar> getFuvarokToBeRatedAsUtas(Integer utasId) {
        return fuvarRepository.getFuvarokToBeRatedAsUtas(utasId);
    }

    public List<Fuvar> getAktivFuvarokByFelhasznalo(Integer felhasznaloId) {
        return fuvarRepository.getAktivFuvarokByFelhasznalo(felhasznaloId);
    }

    public List<Fuvar> getAktivFuvarokByFelhasznalo(String userEmail) {
        Felhasznalo byEmail = felhasznaloRepository.findByEmail(userEmail);
        return fuvarRepository.getAktivFuvarokByFelhasznalo(byEmail.getId());
    }

    public List<Fuvar> getAktivFuvarok(Boolean fromSpecialLocation) {
        return fuvarRepository.getAktivFuvarok(fromSpecialLocation);
    }

    public void deleteAll() {
        fuvarRepository.deleteAll();
    }

    public Fuvar registerNewFuvar(UjSajatFuvarDTO ujfuvar, String soforEmail) {
        Felhasznalo sofor = felhasznaloRepository.findByEmail(soforEmail);
        Location start = Location.builder()
            .address(ujfuvar.getStartAddress())
            .placeId(ujfuvar.getStartPlaceID())
            .lat(ujfuvar.getStartLat())
            .lng(ujfuvar.getStartLng())
            .build();
        Location destination = Location.builder()
            .address(ujfuvar.getDestinationAddress())
            .placeId(ujfuvar.getDestinationPlaceID())
            .lat(ujfuvar.getDestinationLat())
            .lng(ujfuvar.getDestinationLng())
            .build();

        RoadNode roadGraphStartNode, roadGraphDestinationNode;
        if (start.isTheSpecialLocation(appContextHelper)) {
            roadGraphStartNode = roadNetwork.getSpecialLocationNode();
            roadGraphDestinationNode = roadNetwork.getClosestPointInNetwork(destination.toGeoPoint());
        } else {
            roadGraphStartNode = roadNetwork.getClosestPointInNetwork(start.toGeoPoint());
            roadGraphDestinationNode = roadNetwork.getSpecialLocationNode();
        }

        // overwriting the lat-lon with the closest graph node coordinates
        start.setLat(roadGraphStartNode.getPoint().getLatitude());
        start.setLng(roadGraphStartNode.getPoint().getLongitude());
        destination.setLat(roadGraphDestinationNode.getPoint().getLatitude());
        destination.setLng(roadGraphDestinationNode.getPoint().getLongitude());
        destination = locationRepository.save(destination);
        start = locationRepository.save(start);

        Fuvar validFuvar = Fuvar.builder()
            .sofor(sofor)
            .indulasiIdo(parseUjFuvarDTOTime(ujfuvar))        //
            .indulasiHely(ujfuvar.getHonnan())                // to Location
            .uticel(ujfuvar.getHova())                        // to Location
            .capacity(Integer.parseInt(ujfuvar.getCapacity()))
            .rendszam(ujfuvar.getRendszam())                // to Auto
            .info(ujfuvar.getInfo())
            .autoLeiras(ujfuvar.getAutoLeiras())            // to Auto
            .departure(start)
            .destination(destination)
            .meghirdetveDate(now())
            .fromSpecialLocation(start.isTheSpecialLocation(appContextHelper))
            .roadGraphStartNodeId(roadGraphStartNode.getId())
            .roadGraphDestinationNodeId(roadGraphDestinationNode.getId())
            .build();
        fuvarRepository.save(validFuvar);
        return validFuvar;
    }

    public void lemondFuvar(Integer fuvarId, String driverUserName) {
        Optional<Fuvar> byId = fuvarRepository.findById(fuvarId);
        Fuvar fuvar = byId.get();

        if (fuvar.getSofor().getEmail().equals(driverUserName)) {
            emailService.notifyPassengersAboutDeletedRide(fuvar);
            fuvar.setUtasok(null);
            fuvar.setElvallaltIgenyek(null);
            fuvar.setStatusz(FuvarStatusz.LEMONDVA);
            fuvarRepository.save(fuvar);
        }
    }

    public boolean isValid(UjSajatFuvarDTO ujFuvar) {
        return !parseUjFuvarDTOTime(ujFuvar).isBefore(now());
    }

    private LocalDateTime parseUjFuvarDTOTime(UjSajatFuvarDTO ujFuvar) {
        LocalDate parse = LocalDate.parse(ujFuvar.getDatum(), helper.getFrontEndDateTimeFormatter());
        LocalTime of = LocalTime.of(ujFuvar.getOra(), ujFuvar.getPerc());
        return LocalDateTime.of(parse, of);
    }

    @Deprecated
    public List<Fuvar> listByParametersOld(String dateString, String destination) {
        LocalDate date = LocalDate.parse(dateString, helper.getFrontEndDateTimeFormatter());

        //TODO destination mi alapján?
        if (destination.equals("telki-budapest")) {
            return fuvarRepository.listByDateDestination(date, "budapest");
        } else if (destination.equals("budapest-telki")) {
            return fuvarRepository.listByDateDestination(date, "telki");
        }
        return null;
    }

    @Deprecated
    public List<FuvarListItemDTO> listByParametersAPIold(String dateString, String destination) {
        return listByParametersOld(dateString, destination)
            .stream()
            .map(FuvarListItemDTO::new)
            .collect(toList());
    }

    @Deprecated
    public List<Fuvar> listByDestination(String destination) {

        //TODO destination mi alapján?
        if (destination.equals("telki-budapest")) {
            return fuvarRepository.listByDestination("budapest");
        } else if (destination.equals("budapest-telki")) {
            return fuvarRepository.listByDestination("telki");
        }
        return null;
    }

    @Deprecated
    public List<FuvarListItemDTO> listByParametersAPIold(String destination) {
        return listByDestination(destination)
            .stream()
            .map(FuvarListItemDTO::new)
            .collect(toList());
    }

    public List<Fuvar> listByParameters(String dateString, String destination) {
        String dateInput = "";
        String destinationInput = "";
        if (dateString == null || dateString.isEmpty()) {
            dateInput = "%";
        } else {
            LocalDate date = LocalDate.parse(dateString, helper.getFrontEndDateTimeFormatter());
            dateInput = date.format(helper.getDataBaseDateTimeFormatter());
        }

        if (destination != null && destination.equals("telki-budapest")) {
            destinationInput = "%budapest%";
        } else if (destination != null && destination.equals("budapest-telki")) {
            destinationInput = "%telki%";
        } else {
            destinationInput = "%";
        }

        return fuvarRepository.listByDateAndDestination(dateInput, destinationInput);
    }

    public List<FuvarListItemDTO> listByParametersAPI(String dateString, String destination) {
        return listByParameters(dateString, destination)
            .stream()
            .map(FuvarListItemDTO::new)
            .collect(toList());
    }

    public List<Fuvar> getAktivFuvarokByOthers(String userEmail) {
        Felhasznalo byEmail = felhasznaloRepository.findByEmail(userEmail);
        return fuvarRepository.getAktivFuvarokByOther(byEmail.getId());
    }

    public void addPassenger(Integer fuvarId, String userEmail) throws NoCapacityException {
        Fuvar fuvar = fuvarRepository.findById(fuvarId).get();
        if (!fuvar.getStatusz().equals(FuvarStatusz.MEGHIRDETVE)) {
            return;
        }

        Felhasznalo newPassenger = felhasznaloRepository.findByEmail(userEmail);

        if (!fuvar.getUtasok().contains(newPassenger)) {
            if (fuvar.getUtasok().size() >= fuvar.getCapacity()) { //TODO wat
                throw new NoCapacityException();
            }
            fuvar.getUtasok().add(newPassenger);
            fuvarRepository.save(fuvar);
            emailService.notifyDriverAboutNewPassenger(fuvar, newPassenger);
        }
    }

    public void deletePassenger(Integer fuvarId, String userName) {
        Felhasznalo passenger = felhasznaloRepository.findByEmail(userName);
        Optional<Fuvar> byId = fuvarRepository.findById(fuvarId);
        Fuvar fuvar = byId.get();

        if (fuvar.getUtasok().contains(passenger)) {
            fuvar.getUtasok().remove(passenger);
            fuvarRepository.save(fuvar);
        } else if (fuvar.getElvallaltIgenyek().stream().map(Igeny::getHirdeto).collect(toList()).contains(passenger)) {
            for (Igeny igeny : fuvar.getElvallaltIgenyek()) {
                if (igeny.getHirdeto().equals(passenger)) {
                    igenyService.removeFuvar(fuvarId, igeny.getId());
                }
            }
        } else {
            //TODO hiba
        }
    }

    public List<Fuvar> getUsersAppliedFuvarok(String userEmail) {
        Felhasznalo utas = felhasznaloRepository.findByEmail(userEmail);
        return fuvarRepository.getAllByUtas(utas.getId());
    }

    public Fuvar getFuvarDetailsById(Integer fuvarId) {
        Fuvar fuvarById = fuvarRepository.getFuvarById(fuvarId);
        return fuvarById;
    }

    public Boolean isDriver(Integer fuvarId, String name) {
        return fuvarRepository.isDriver(fuvarId, name) == 1;
    }

    public Boolean isPassenger(Integer fuvarId, String name) {
        //return fuvarRepository.isPassenger(fuvarId, name) == 1;
        Fuvar fuvar = fuvarRepository.getFuvarById(fuvarId);
        Felhasznalo felhasznalo = felhasznaloRepository.findByEmail(name);

        return fuvar.getOsszesUtas().contains(felhasznalo);
    }

}
