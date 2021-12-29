package tmit.bme.telkicar.service;

import tmit.bme.telkicar.domain.user.Felhasznalo;
import tmit.bme.telkicar.security.NewPasswordDTO;
import tmit.bme.telkicar.security.RegisterFelhasznaloDTO;

public interface IFelhasznaloService {
	Felhasznalo registerNewUserAccount(RegisterFelhasznaloDTO accountDto);

	void changePassword(String userName, NewPasswordDTO passwordDto) throws oldPasswordIncorrectException;
}
