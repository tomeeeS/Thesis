package tmit.bme.telkicar.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tmit.bme.telkicar.CustomProperties;
import tmit.bme.telkicar.domain.user.Felhasznalo;
import tmit.bme.telkicar.domain.advert.Fuvar;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class EmailService {

	@Autowired
	public JavaMailSender emailSender;
	@Autowired
	private CustomProperties properties;

	private Logger logger = LoggerFactory.getLogger(getClass().getName());

//	public void sendSimpleMessage(String to, String subject, String text) {
//		SimpleMailMessage message = new SimpleMailMessage();
//		message.setTo(to);
//		message.setSubject(subject);
//		message.setText(text);
//		emailSender.send(message);
//	}

	@Async
	public void sendRegConfirmEmail(String toEmail, String kerersztNev) {
		if (!properties.getEnabled()) return; //TODO refactor enabled
		try {
			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(toEmail);
			helper.setSubject("Telki Ride-Share");
			helper.setText(
					"<html>" +
							"<body>" +
							"<h1>Kedves " + kerersztNev + "!</h1> " +
							"<p>Köszöntünk Telki fuvar-megosztó oldalán, mostantól meghirdetheted üres üléseidet és jelentkezhetsz mások mellé utasnak.</p>" +
							"<p> <a href='http://localhost:8080'>Ezen az oldalon</a> jelentkezhetsz be. </p>" +
							"</html>"//TODO
					, true);
			emailSender.send(message);
			logger.info("Sending registration confirm email to: " + toEmail);
		} catch (MessagingException e) {
			e.printStackTrace();    //TODO
		}
	}

	@Async
	public void notifyDriverAboutNewPassenger(Fuvar fuvar, Felhasznalo newPassenger) {
		if (!properties.getEnabled()) return;
		try {
			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(fuvar.getSofor().getEmail());
			helper.setSubject("Telki Ride-Share");
			helper.setText(
					"<html>" +
							"<body>" +
							"<h1>Kedves " + fuvar.getSofor().getKeresztNev() + "!</h1> " +
							"<p>Egy új utas jelentkezett a fuvarodra: " + newPassenger.getFullName() + "(tel: " + newPassenger.getTel() + ") </p>" +
							"fauvar adatai" + //TODO fuvar adatai
							"<p> <a href='http://localhost:8080/fuvar/" + fuvar.getId() + "'>Ezen az oldalon</a> megnézheted a fuvar részleteit. </p>" +
							"</html>"
					, true);
			emailSender.send(message);
			logger.info("Sending new passenger notification to driver: " + fuvar.getSofor().getEmail());
		} catch (MessagingException e) {
			e.printStackTrace();    //TODO
		}
	}

	public void notifyPassengersAboutDeletedRide(Fuvar fuvar) {
		if (!properties.getEnabled()) return;
		for (Felhasznalo utas : fuvar.getOsszesUtas()) {
			try {
				MimeMessage message = emailSender.createMimeMessage();
				MimeMessageHelper helper = new MimeMessageHelper(message, true);
				helper.setTo(utas.getEmail());
				helper.setSubject("Telki Ride-Share");
				helper.setText(
						"<html>" +
								"<body>" +
								"<h1>Kedves " + utas.getKeresztNev() + "!</h1> " +
								"<p>A sofőr lemondta az utat amire jelentkeztél</p>" +
								"<p>Indulási idő:" + fuvar.getIndulasiIdoString() +  " uticél:" + fuvar.getUticel() + "</p>" +
								"</html>"//TODO
						, true);
				emailSender.send(message);
				logger.info("Sending notification about cancelled ride to: " + utas.getEmail());
			} catch (MessagingException e) {
				e.printStackTrace();    //TODO
			}
		}

	}
}
