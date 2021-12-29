package tmit.bme.telkicar.dao;

import org.springframework.data.repository.CrudRepository;
import tmit.bme.telkicar.domain.user.Auto;

public interface AutoRepository extends CrudRepository<Auto, Integer> {
}
