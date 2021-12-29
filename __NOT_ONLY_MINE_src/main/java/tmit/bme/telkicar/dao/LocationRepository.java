package tmit.bme.telkicar.dao;

import org.springframework.data.repository.CrudRepository;
import tmit.bme.telkicar.domain.advert.Location;

public interface LocationRepository  extends CrudRepository<Location, Integer> {
}
