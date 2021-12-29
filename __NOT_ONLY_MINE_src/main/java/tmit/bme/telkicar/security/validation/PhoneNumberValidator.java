package tmit.bme.telkicar.security.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

	private Pattern pattern;
	private Matcher matcher;
	private static final String PHONE_NUMBER_PATTERN = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s0-9]+$";

	@Override
	public void initialize(ValidPhoneNumber constraintAnnotation) {
	}

	@Override
	public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
		return (validatePhoneNumber(phoneNumber));
	}

	private boolean validatePhoneNumber(String email) {
		pattern = Pattern.compile(PHONE_NUMBER_PATTERN);
		matcher = pattern.matcher(email);
		return matcher.matches();
	}
}