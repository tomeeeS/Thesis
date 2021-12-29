package tmit.bme.telkicar.dao;

import org.springframework.data.repository.CrudRepository;
import tmit.bme.telkicar.domain.user.Role;

public interface RoleRepository extends CrudRepository<Role, Integer> {
}
