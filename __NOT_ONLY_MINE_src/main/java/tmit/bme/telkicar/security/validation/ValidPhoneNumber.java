package tmit.bme.telkicar.security.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = PhoneNumberValidator.class)
@Documented
public @interface ValidPhoneNumber {
	String message() default "A telefonszámban csak +, szóköz, -, 0-9 karakterek szerepelhetnek.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
