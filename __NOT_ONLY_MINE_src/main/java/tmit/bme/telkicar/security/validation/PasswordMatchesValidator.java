package tmit.bme.telkicar.security.validation;

import tmit.bme.telkicar.security.RegisterFelhasznaloDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

	@Override
	public void initialize(PasswordMatches constraintAnnotation) {
	}

	@Override
	public boolean isValid(Object obj, ConstraintValidatorContext context) {
		RegisterFelhasznaloDTO user = (RegisterFelhasznaloDTO) obj;
		return user.getPassword().equals(user.getMatchingPassword());
	}
}
