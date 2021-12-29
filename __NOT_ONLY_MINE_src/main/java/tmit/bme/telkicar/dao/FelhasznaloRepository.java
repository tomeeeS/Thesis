package tmit.bme.telkicar.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import tmit.bme.telkicar.domain.user.Felhasznalo;

import java.util.List;

public interface FelhasznaloRepository extends CrudRepository<Felhasznalo, Integer> {

	@Query(value = "SELECT kereszt_nev, vezetek_nev FROM felhasznalo", nativeQuery = true)
	List<Object> listFelhasznalok();
	//todo dto

	Felhasznalo findByEmail(String email);

}