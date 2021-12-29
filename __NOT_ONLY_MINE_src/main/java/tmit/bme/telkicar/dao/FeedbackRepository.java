package tmit.bme.telkicar.dao;

import org.springframework.data.repository.CrudRepository;
import tmit.bme.telkicar.domain.advert.Feedback;

public interface FeedbackRepository extends CrudRepository<Feedback, Integer> {

}
