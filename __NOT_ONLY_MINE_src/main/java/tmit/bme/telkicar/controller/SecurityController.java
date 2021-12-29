package tmit.bme.telkicar.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import tmit.bme.telkicar.security.NewPasswordDTO;
import tmit.bme.telkicar.security.RegisterFelhasznaloDTO;
import tmit.bme.telkicar.service.IFelhasznaloService;
import tmit.bme.telkicar.service.oldPasswordIncorrectException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;

@Controller
public class SecurityController {

	@Autowired
	private IFelhasznaloService userService;

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/user/registration")
	public String showRegistrationForm(WebRequest request, Model model) {
		RegisterFelhasznaloDTO userDto = new RegisterFelhasznaloDTO();
		model.addAttribute("user", userDto);
		return "registration";
	}

	@RequestMapping(value = "/user/registration", method = RequestMethod.POST)
	public String registerUserAccount(@ModelAttribute("user") @Valid final RegisterFelhasznaloDTO userDto, BindingResult result, final HttpServletRequest request, final Errors errors) {
		if (result.hasErrors()) {
			LOGGER.info("Error registering user account with information: {}", userDto.getEmail()); //TODO log more
			//TODO error handling in response
			return "registration";
		} else {
			userService.registerNewUserAccount(userDto);
			LOGGER.info("Registering user account with information: {}", userDto.getEmail());
			return "login";
		}
	}

	@GetMapping("/user/password-change")
	public String passwordChangeForm(WebRequest request, Model model) {
		NewPasswordDTO passwordDto = new NewPasswordDTO();
		model.addAttribute("password", passwordDto);
		return "password-change";
	}

	@RequestMapping(value = "/user/password-change", method = RequestMethod.POST)
	public String changeUserPassword(@ModelAttribute("password") @Valid final NewPasswordDTO passwordDto, BindingResult result, final HttpServletRequest request, final Errors errors, Principal principal) {
		if (result.hasErrors()) {
			LOGGER.info("User password change failed: {}", principal.getName());
			return "password-change";
		} else {

			try {
				userService.changePassword(principal.getName(), passwordDto);
				LOGGER.info("Changed user password: {}", principal.getName());
				return "user-profile";
			} catch (oldPasswordIncorrectException e) {
				LOGGER.info("User password change failed: {}", principal.getName());
				result.rejectValue("oldPassword", "error.pw", "Hibás jelszó.");
				return "password-change";
			}

		}
	}

}
