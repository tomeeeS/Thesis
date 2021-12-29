package tmit.bme.telkicar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tmit.bme.telkicar.notification.EmailService;
import tmit.bme.telkicar.dao.FelhasznaloRepository;
import tmit.bme.telkicar.dao.RoleRepository;
import tmit.bme.telkicar.domain.user.Auto;
import tmit.bme.telkicar.domain.user.Felhasznalo;
import tmit.bme.telkicar.domain.advert.Fuvar;
import tmit.bme.telkicar.domain.user.Role;
import tmit.bme.telkicar.security.NewPasswordDTO;
import tmit.bme.telkicar.security.RegisterFelhasznaloDTO;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class FelhasznaloService implements IFelhasznaloService {
	@Autowired
	private FelhasznaloRepository felhasznaloRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private EmailService emailService;
	@Autowired
	private IgenyService igenyService;

	@Transactional
	@Override
	public Felhasznalo registerNewUserAccount(RegisterFelhasznaloDTO accountDto) {
		if (emailExist(accountDto.getEmail())) {
			return null;
		}

		final Felhasznalo user = new Felhasznalo();
		user.setKeresztNev(accountDto.getFirstName());
		user.setVezetekNev(accountDto.getLastName());
		user.setTel(accountDto.getTel());
		user.setEnabled(1);
		user.setEmail(accountDto.getEmail());
		user.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        user.setDistanceFlexibilityPercentage(accountDto.getDistanceFlexibilityPercentage());
		user.setTimeFlexibilityPercentage(accountDto.getTimeFlexibilityPercentage());
		user.setKredit(10);

		Felhasznalo newUser = felhasznaloRepository.save(user);
		roleRepository.save(Role.builder().felhasznalo(newUser).authority("ROLE_USER").build());

		emailService.sendRegConfirmEmail(user.getEmail(), user.getKeresztNev());
		return newUser;
	}

	@Override
	public void changePassword(String userName, NewPasswordDTO passwordDto) throws oldPasswordIncorrectException {
		Felhasznalo user = felhasznaloRepository.findByEmail(userName);

		if (passwordEncoder.matches(passwordDto.getOldPassword(), user.getPassword())) {
			user.setPassword(passwordEncoder.encode((passwordDto.getNewPassword())));
			felhasznaloRepository.save(user);
		} else {
			throw new oldPasswordIncorrectException();
		}
	}

	public boolean emailExist(String email) {
		return felhasznaloRepository.findByEmail(email) != null;
	}

	public Felhasznalo getUserByUserName(String email) {
		return felhasznaloRepository.findByEmail(email);
	}

	public Boolean canGiveALift(Integer hirdetesId, String name) {
		//TODO query?
		//TODO azonos irányú hirdetés
		//TODO !!! csak hirdetés idejében lévő fuvarokból
		List<Fuvar> sajatFuvarok = felhasznaloRepository.findByEmail(name).getSajatFuvarok();
		Integer utasokSzama = igenyService.getIgenyDetailsById(hirdetesId).getUtasokSzama();
		for ( Fuvar f: sajatFuvarok) {
			if (f.getSzabadHely() >= utasokSzama) {
				return true;
			}
		}
		return false;
	}

	public void addCar(String userName, Auto car) {
		Felhasznalo user = felhasznaloRepository.findByEmail(userName);
		user.getAutok().add(car);
		felhasznaloRepository.save(user);
	}

}