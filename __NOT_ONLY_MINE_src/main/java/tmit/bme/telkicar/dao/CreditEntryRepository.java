package tmit.bme.telkicar.dao;

import org.springframework.data.repository.CrudRepository;
import tmit.bme.telkicar.domain.user.CreditEntry;

import java.util.List;

public interface CreditEntryRepository extends CrudRepository<CreditEntry, Integer> {

	List<CreditEntry> getAllByUserIdOrderByTimestampDesc(Integer userId);
}
