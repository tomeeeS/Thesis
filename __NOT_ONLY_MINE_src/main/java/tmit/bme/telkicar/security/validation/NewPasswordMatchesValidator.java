package tmit.bme.telkicar.security.validation;

import tmit.bme.telkicar.security.NewPasswordDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NewPasswordMatchesValidator implements ConstraintValidator<NewPasswordMatches, Object> {

	@Override
	public void initialize(NewPasswordMatches constraintAnnotation) {
	}

	@Override
	public boolean isValid(Object obj, ConstraintValidatorContext context) {
		NewPasswordDTO pw = (NewPasswordDTO) obj;
		return pw.getNewPassword().equals(pw.getMatchingNewPassword());
	}
}
