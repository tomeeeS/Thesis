package tmit.bme.telkicar.security;

import lombok.*;
import tmit.bme.telkicar.security.validation.PasswordMatches;
import tmit.bme.telkicar.security.validation.UniqueEmail;
import tmit.bme.telkicar.security.validation.ValidEmail;
import tmit.bme.telkicar.security.validation.ValidPhoneNumber;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@PasswordMatches
@UniqueEmail
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterFelhasznaloDTO {

	@NotNull
	@NotEmpty
	private String firstName;

	@NotNull
	@NotEmpty
	private String lastName;

	@NotNull
	@NotEmpty
	private String password;

	@NotNull
	@NotEmpty
	private String matchingPassword;

	@NotNull
	@NotEmpty
	@ValidEmail
	private String email;

	@NotNull
	@NotEmpty
	@ValidPhoneNumber
	private String tel;

	@Builder.Default
	private Double timeFlexibilityPercentage = 50.0;

	@Builder.Default
	private Double distanceFlexibilityPercentage = 50.0;
}
