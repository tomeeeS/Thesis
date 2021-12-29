package tmit.bme.telkicar.security;

import lombok.Getter;
import lombok.Setter;
import tmit.bme.telkicar.security.validation.NewPasswordMatches;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NewPasswordMatches
public class NewPasswordDTO {
	@NotNull
	@NotEmpty
	String oldPassword;

	@NotNull
	@NotEmpty
	String newPassword;

	@NotNull
	@NotEmpty
	String matchingNewPassword;
}
