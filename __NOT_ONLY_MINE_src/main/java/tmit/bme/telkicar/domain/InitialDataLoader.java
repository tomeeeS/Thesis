package tmit.bme.telkicar.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tmit.bme.telkicar.dao.*;
import tmit.bme.telkicar.domain.advert.*;
import tmit.bme.telkicar.domain.user.CreditEntry;
import tmit.bme.telkicar.domain.user.Felhasznalo;
import tmit.bme.telkicar.domain.user.Rating;
import tmit.bme.telkicar.domain.user.Role;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static java.time.LocalDateTime.now;

@Component
public class InitialDataLoader {

    @Autowired
    private FelhasznaloRepository felhasznaloRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private FuvarRepository fuvarRepository;
    //	@Autowired
//	private FeedbackRepository feedbackRepository;
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CreditEntryRepository creditRepository;
    @Autowired
    private LocationRepository locationRepository;

    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    @PostConstruct
    public void init() {
        Felhasznalo admin = createAdmin();
        createDemoFuvar(admin);

        logger.info("ALL DATA LOADING FINISHED.");
    }

    private Felhasznalo createAdmin() {
        String email = "admin@admin.hu";
        if (felhasznaloRepository.findByEmail(email) == null) {
            Felhasznalo testadmin = Felhasznalo.builder()
                .email(email)                                        //kötelező security miatt
                .password(passwordEncoder.encode("a"))  //kötelező security miatt
                .enabled(1)                                            //kötelező security miatt
                .vezetekNev("Teszt")
                .keresztNev("Admin")
                .tel("0630 303 0303")
                .kredit(15)
                .build();
            felhasznaloRepository.save(testadmin);
            roleRepository.save(Role.builder().felhasznalo(testadmin).authority("ROLE_USER").build());
            roleRepository.save(Role.builder().felhasznalo(testadmin).authority("ROLE_ADMIN").build());

            return testadmin;
        } else {
//			System.out.println("testadmin already exists:");
            return felhasznaloRepository.findByEmail(email);
//			System.out.println(byEmail.getId() + ", " + byEmail.getEmail());
        }

    }

    private void createDemoFuvar(Felhasznalo testadmin) {
        String email1 = "vegh.bela@mail.test";
        String email2 = "liz.bien@mail.test";
        if (felhasznaloRepository.findByEmail(email1) == null) {
            //teljesitett fuvarok
            Felhasznalo bela = Felhasznalo.builder()
                .password(passwordEncoder.encode("a"))
                .enabled(1)
                .email(email1)
                .vezetekNev("Végh")
                .keresztNev("Béla")
                .tel("0670 707 0707")
                .kredit(15)
                .build();
            felhasznaloRepository.save(bela);
            roleRepository.save(Role.builder().felhasznalo(bela).authority("ROLE_USER").build());

            Location telki = Location.builder()
                .address("Telki, 2089")
                .placeId("ChIJC01pqgZ0akcRgKYeDCnEAAQ")
                .lat(47.5498f)
                .lng(18.8246f)
                .build();
            locationRepository.save(telki);
            Location oktogon = Location.builder()
                .address("Budapest, Oktogon")
                .placeId("EhpCdWRhcGVzdCwgT2t0b2dvbiwgSHVuZ2FyeSIuKiwKFAoSCaP41nlu3EFHETf06g8AcstzEhQKEgnJz9TRNMNBRxFgER4MKcQABA")
                .lat(47.5055f)
                .lng(19.0637f)
                .build();
            locationRepository.save(oktogon);

            Fuvar fuvar = Fuvar.builder()
                .rendszam("asd-123")
                .indulasiIdo(now().plusDays(1010))
                .capacity(3)
                .indulasiHely("telki")
                .uticel("BME")
                .departure(telki)
                .destination(oktogon)
                .autoLeiras("Toyota Prius")
                .info("info")
                .sofor(testadmin)
                .utasok(Collections.singleton(felhasznaloRepository.findByEmail(email1)))
                .statusz(FuvarStatusz.TELJESITVE)
                .meghirdetveDate(now())
                .lezarvaDate(now())
                .teljesitveDate(now())
                .build();
            fuvarRepository.save(fuvar);

//			Feedback noice_ride_mate = Feedback.builder()
//					.felhasznalo(bela)
//					.fuvar(fuvar)
//					.role(FeedbackRole.PASSENGER)
//					.successful(true)
//					.description("noice ride mate")
//					.build();
//			feedbackRepository.save(noice_ride_mate);
//
//			Feedback eh = Feedback.builder()
//					.felhasznalo(testadmin)
//					.fuvar(fuvar)
//					.role(FeedbackRole.DRIVER)
//					.successful(true)
//					.build();
//			feedbackRepository.save(eh);

            Rating rating = Rating.builder()
                .felhasznalo(testadmin)
                .rating(5)
                .time(now())
                .build();
            ratingRepository.save(rating);

            //feedback demo
            Felhasznalo liz = Felhasznalo.builder()
                .password(passwordEncoder.encode("a"))
                .enabled(1)
                .email(email2)
                .vezetekNev("Bien")
                .keresztNev("Liz")
                .tel("0670 707 0707")
                .kredit(10)
                .build();
            felhasznaloRepository.save(liz);
            roleRepository.save(Role.builder().felhasznalo(liz).authority("ROLE_USER").build());

            fuvar = Fuvar.builder()
                .rendszam("asd-123")
                .indulasiIdo(now().minusDays(1))
                .capacity(3)
                .indulasiHely("telki")
                .uticel("BME")
                .departure(telki)
                .destination(oktogon)
                .autoLeiras("Toyota Prius")
                .info("info")
                .sofor(testadmin)
                .utasok(Collections.singleton(bela))
                .statusz(FuvarStatusz.LEZARVA)
                .meghirdetveDate(now())
                .lezarvaDate(now())
                .teljesitveDate(now())
                .build();
            fuvarRepository.save(fuvar);

            fuvar = Fuvar.builder()
                .rendszam("asd-123")
                .indulasiIdo(now().minusDays(2))
                .capacity(3)
                .indulasiHely("telki")
                .uticel("BME")
                .departure(telki)
                .destination(oktogon)
                .autoLeiras("Toyota Prius")
                .info("info")
                .sofor(testadmin)
                .utasok(new HashSet<>(Arrays.asList(bela, liz)))
                .statusz(FuvarStatusz.LEZARVA)
                .meghirdetveDate(now())
                .lezarvaDate(now())
                .teljesitveDate(now())
                .build();
            fuvarRepository.save(fuvar);

            fuvar = Fuvar.builder()
                .rendszam("asd-123")
                .indulasiIdo(now().minusDays(2))
                .capacity(3)
                .indulasiHely("telki")
                .uticel("BME")
                .departure(telki)
                .destination(oktogon)
                .autoLeiras("Toyota Prius")
                .info("info")
                .sofor(bela)
                .utasok(new HashSet<>(Arrays.asList(testadmin, liz)))
                .statusz(FuvarStatusz.LEZARVA)
                .meghirdetveDate(now())
                .lezarvaDate(now())
                .teljesitveDate(now())
                .build();
            fuvarRepository.save(fuvar);

            //Credit
            creditRepository.save(CreditEntry.builder()
                .fuvar(fuvar)
                .user(testadmin)
                .valueChange(1)
                .timestamp(now())
                .build());

        }
    }

}
