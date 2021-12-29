package tmit.bme.telkicar.security.validation;

import org.springframework.beans.factory.annotation.Autowired;
import tmit.bme.telkicar.security.RegisterFelhasznaloDTO;
import tmit.bme.telkicar.service.FelhasznaloService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, Object> {

   @Autowired
   private FelhasznaloService felhasznaloService;

   public void initialize(UniqueEmail constraint) {
   }

   public boolean isValid(Object obj, ConstraintValidatorContext context) {
      RegisterFelhasznaloDTO newFelhasznalo = (RegisterFelhasznaloDTO) obj;
      return !felhasznaloService.emailExist(newFelhasznalo.getEmail());
   }
}
