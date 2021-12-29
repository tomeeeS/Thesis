package tmit.bme.telkicar.dao;

import org.springframework.data.repository.CrudRepository;
import tmit.bme.telkicar.domain.user.Rating;

public interface RatingRepository extends CrudRepository<Rating, Integer> {
}
