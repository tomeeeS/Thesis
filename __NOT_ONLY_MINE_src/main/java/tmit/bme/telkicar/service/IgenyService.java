package tmit.bme.telkicar.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tmit.bme.telkicar.controller.dto.UjFuvarIgenyDTO;
import tmit.bme.telkicar.controller.dto.api.IgenyListItemDTO;
import tmit.bme.telkicar.controller.dto.api.IgenySearchDTO;
import tmit.bme.telkicar.dao.FelhasznaloRepository;
import tmit.bme.telkicar.dao.IgenyRepository;
import tmit.bme.telkicar.dao.LocationRepository;
import tmit.bme.telkicar.domain.advert.Fuvar;
import tmit.bme.telkicar.domain.advert.Igeny;
import tmit.bme.telkicar.domain.advert.Location;
import tmit.bme.telkicar.domain.user.Felhasznalo;
import tmit.bme.telkicar.logic.geography.roadnetwork.RoadNetwork;
import tmit.bme.telkicar.logic.geography.roadnetwork.RoadNode;
import tmit.bme.telkicar.logic.helpers.AppContextHelper;
import tmit.bme.telkicar.notification.EmailService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IgenyService {

    @Autowired
    private IgenyRepository igenyRepository;
    @Autowired
    private FelhasznaloRepository felhasznaloRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ServiceHelper helper;
    @Autowired
    private FuvarService fuvarService;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private RoadNetwork roadNetwork;
    @Autowired
    private AppContextHelper appContextHelper;

    public void registerNewIgeny(UjFuvarIgenyDTO ujIgeny, String name) {
        Felhasznalo hirdeto = felhasznaloRepository.findByEmail(name);
        Location start = Location.builder()
            .address(ujIgeny.getStartAddress())
            .placeId(ujIgeny.getStartPlaceID())
            .lat(ujIgeny.getStartLat())
            .lng(ujIgeny.getStartLng())
            .build();
        Location destination = Location.builder()
            .address(ujIgeny.getDestinationAddress())
            .placeId(ujIgeny.getDestinationPlaceID())
            .lat(ujIgeny.getDestinationLat())
            .lng(ujIgeny.getDestinationLng())
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

        Igeny igeny = Igeny.builder()
            .indulasiHely(ujIgeny.getHonnan())
            .uticel(ujIgeny.getHova())
            .indulasiIdo(parseDTOTime(ujIgeny))
            .utasokSzama(ujIgeny.getUtasokSzama())
            .info(ujIgeny.getInfo())
            .hirdeto(hirdeto)
            .departure(start)
            .roadGraphStartNodeId(roadGraphStartNode.getId())
            .roadGraphDestinationNodeId(roadGraphDestinationNode.getId())
            .destination(destination)
            .fromSpecialLocation(start.isTheSpecialLocation(appContextHelper))
            .build();

        igenyRepository.save(igeny);
    }

    public void deleteIgeny(Integer igenyId, String userName) {
        Igeny igeny = igenyRepository.findById(igenyId).get();
        if (igeny.getHirdeto().getEmail().equals(userName)) {
            igenyRepository.deleteById(igenyId);
        }
    }

    private LocalDateTime parseDTOTime(UjFuvarIgenyDTO ujIgeny) {
        LocalDate parse = LocalDate.parse(ujIgeny.getDatum(), helper.getFrontEndDateTimeFormatter());
        LocalTime of = LocalTime.of(ujIgeny.getOra(), ujIgeny.getPerc());
        return LocalDateTime.of(parse, of);
    }

    public List<Igeny> listByParameters(String dateString, String destination) {
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

        return igenyRepository.listByDateDestination(dateInput, destinationInput);
    }

    public List<IgenyListItemDTO> listAPIByParameters(String dateString, String destination) {
        return listByParameters(dateString, destination)
            .stream()
            .map(IgenyListItemDTO::new)
            .collect(Collectors.toList());
    }

    public Igeny getIgenyDetailsById(Integer hirdetesId) {
        return igenyRepository.findById(hirdetesId).get();
    }

    public Boolean isAdvertiser(Integer hirdetesId, String name) {
        //TODO in query ?
        return igenyRepository.findById(hirdetesId).get().getHirdeto().getEmail().equals(name);
    }

    public void attachFuvar(Integer fuvarId, Integer igenyId) {
        Igeny igeny = igenyRepository.findById(igenyId).get();
        Fuvar fuvar = fuvarService.getFuvarDetailsById(fuvarId);

        if (!fuvar.getElvallaltIgenyek().contains(igeny)) {
            if (fuvar.getSzabadHely() < igeny.getUtasokSzama()) {
                throw new NoCapacityException();
            }
            igeny.setFuvar(fuvar);
            igenyRepository.save(igeny);
        }
    }

    public void removeFuvar(Integer fuvarId, Integer igenyId) {
        Igeny igeny = igenyRepository.findById(igenyId).get();
        Fuvar fuvar = fuvarService.getFuvarDetailsById(fuvarId);
        if (igeny.getFuvar().equals(fuvar)) {
            igeny.setFuvar(null);
            igenyRepository.save(igeny);
        }
    }

    public List<Igeny> getAktivIgenyekByFelhasznalo(String username) {
        Felhasznalo byEmail = felhasznaloRepository.findByEmail(username);
        return igenyRepository.getAktivIgenyByFelhasznalo(byEmail.getId());
    }

    @NotNull
    public List<Igeny> getAktivIgenyek(Boolean fromSpecialLocation) {
        return igenyRepository.getAktivIgenyek(fromSpecialLocation);
    }

    public List<IgenyListItemDTO> searchByParametersAPI(IgenySearchDTO igenySearch) {

        LocalDate startDate = helper.getDateFromFrontEndPattern(igenySearch.getStartDateString());
        LocalDateTime startTime = null;
        if (startDate != null) {
            startTime = startDate.atTime(igenySearch.getStartHour(), igenySearch.getStartMinute());
        }

        List<IgenyListItemDTO> igenyListItemDTOS = new ArrayList<>();
        igenyRepository.search(
            startTime
//				igenySearch.getStartLat(),
//				igenySearch.getStartLng(),
//				igenySearch.getDestinationLat(),
//				igenySearch.getDestinationLng(),
//				upperStartDistanceLimitMeters,
//				upperDestinationDistanceLimitMeters
        ).forEach(i -> {
            igenyListItemDTOS.add(new IgenyListItemDTO(i));
        });
        return igenyListItemDTOS;
    }

    public void deleteAll() {
        igenyRepository.deleteAll();
    }
}
