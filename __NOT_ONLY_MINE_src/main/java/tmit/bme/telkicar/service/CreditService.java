package tmit.bme.telkicar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tmit.bme.telkicar.dao.*;
import tmit.bme.telkicar.domain.advert.Feedback;
import tmit.bme.telkicar.domain.advert.FeedbackRole;
import tmit.bme.telkicar.domain.advert.Fuvar;
import tmit.bme.telkicar.domain.user.CreditEntry;
import tmit.bme.telkicar.domain.user.Felhasznalo;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static tmit.bme.telkicar.domain.advert.FeedbackRole.DRIVER;

@Service
public class CreditService {

	@Autowired
	private CreditEntryRepository creditRepository;
	@Autowired
	private FelhasznaloRepository felhasznaloRepository;
	@Autowired
	private CreditEntryRepository entryRepository;

	public void distributeUserCredits(Fuvar fuvar) {
		HashSet<Felhasznalo> passengersWhoVerifiedDriver = fuvar.getFeedbacks().stream()
				.filter(f -> f.getRole().equals(FeedbackRole.PASSENGER))
				.filter(f -> f.getVerifiedUsers() != null)
				.map(Feedback::getReviewer)
				.collect(Collectors.toCollection(HashSet::new));

		Feedback driverFeedback = fuvar.getFeedbacks().stream()
				.filter(f -> f.getRole().equals(FeedbackRole.DRIVER)).findFirst().get();
		HashSet<Felhasznalo> passengersVerifiedByDriver = new HashSet<>(driverFeedback.getVerifiedUsers());

		passengersWhoVerifiedDriver.retainAll(passengersVerifiedByDriver);

		addTransferCreditsPassengerToDriver(fuvar, passengersWhoVerifiedDriver);
	}

	public void distributeUserCreditsWithIncompleteFeedbacks(Fuvar fuvar) {
		Optional<Feedback> driverFeedbackOptional = fuvar.getFeedbacks().stream().filter(f -> f.getRole().equals(DRIVER)).findFirst();
		boolean hasDriverFeedback = driverFeedbackOptional.isPresent();

		HashSet<Felhasznalo> allPassengers = new HashSet<>(fuvar.getUtasok());
		HashSet<Felhasznalo> passengersGaveFeedback = fuvar.getFeedbacks().stream()
				.filter(f -> f.getRole().equals(FeedbackRole.PASSENGER))
				.map(Feedback::getReviewer)
				.collect(Collectors.toCollection(HashSet::new));
		allPassengers.removeAll(passengersGaveFeedback); //passengersGaveNoFeedback

		HashSet<Felhasznalo> passengersWhoVerifiedDriver = fuvar.getFeedbacks().stream()
				.filter(f -> f.getRole().equals(FeedbackRole.PASSENGER))
				.filter(f -> f.getVerifiedUsers() != null)
				.map(Feedback::getReviewer)
				.collect(Collectors.toCollection(HashSet::new));

		if (hasDriverFeedback) {
			// aki sofor, utas ok metszet
			Feedback driverFeedback = driverFeedbackOptional.get();
			HashSet<Felhasznalo> passengersVerifiedByDriver = new HashSet<>(driverFeedback.getVerifiedUsers());
			passengersWhoVerifiedDriver.retainAll(passengersVerifiedByDriver); // intersection
		} else {
			// aki szerint sofor ok + aki nem adott
			passengersWhoVerifiedDriver.addAll(allPassengers);// + passengersGaveNoFeedback
		}

		addTransferCreditsPassengerToDriver(fuvar, passengersWhoVerifiedDriver);
	}

	private void addTransferCreditsPassengerToDriver(Fuvar fuvar, HashSet<Felhasznalo> passengersWhoVerifiedDriver) {
		if (!passengersWhoVerifiedDriver.isEmpty()) {
			Felhasznalo sofor = fuvar.getSofor();
			sofor.addKredit(passengersWhoVerifiedDriver.size());
			felhasznaloRepository.save(sofor);
			entryRepository.save(CreditEntry.builder()
					.user(sofor)
					.timestamp(LocalDateTime.now())
					.fuvar(fuvar)
					.valueChange(passengersWhoVerifiedDriver.size())
					.build());

			passengersWhoVerifiedDriver.forEach(f -> {
				f.removeKredit(1);
				felhasznaloRepository.save(f);
				entryRepository.save(CreditEntry.builder()
						.user(f)
						.timestamp(LocalDateTime.now())
						.fuvar(fuvar)
						.valueChange(-1)
						.build());
			});
		}

	}

	public List<CreditEntry> getCreditEntriesForUser(Felhasznalo user) {
		return getCreditEntriesForUser(user.getId());
	}

	public List<CreditEntry> getCreditEntriesForUser(Integer userId) {
		return creditRepository.getAllByUserIdOrderByTimestampDesc(userId);
	}
}
