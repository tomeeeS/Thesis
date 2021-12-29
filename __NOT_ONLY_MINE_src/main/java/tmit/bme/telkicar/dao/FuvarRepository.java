package tmit.bme.telkicar.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tmit.bme.telkicar.domain.advert.Fuvar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FuvarRepository extends CrudRepository<Fuvar, Integer> {

	@Query(value = "SELECT * " +
			"FROM fuvar " +
//			"JOIN location departure on fuvar.departure_id = departure.id " +
//			"JOIN location destination on fuvar.destination_id = destination.id " +
			"JOIN (select fuvar.id, count(fuvar_id) utasok from fuvar " +
			"join felhasznalo_fuvar ff on fuvar.id = ff.fuvar_id " +
			"group by fuvar.id) capacity_select on fuvar.id = capacity_select.id " +
			"WHERE statusz = 'MEGHIRDETVE' " +
			"  AND  fuvar.capacity > capacity_select.utasok" + //TODO test
			"  AND (:date is null OR DATE(fuvar.indalasi_ido) = DATE(:date)) " //+
//			"  AND (:startLat is null OR " +
//			"       ST_Distance_Sphere( " +
//			"               point(departure.lat, departure.lng), " +
//			"               point(:startLat, :startLng) " +
//			"           ) < :startUpperLimit) " +
//			"  AND (:destinationLat is null OR " +
//			"       ST_Distance_Sphere( " +
//			"               point(destination.lat, destination.lng), " +
//			"               point(:destinationLat, :destinationLng) " +
//			"           ) < :destinationUpperLimit) " +
//			"ORDER BY ABS(TIMEDIFF(fuvar.indalasi_ido, :date)), " +
//			"         ST_Distance_Sphere( " +
//			"                 point(destination.lat, destination.lng), " +
//			"                 point(:destinationLat, :destinationLng) " +
//			"             )"
			,
			nativeQuery = true)
	List<Fuvar> search(
			@Param("date") LocalDateTime date
//			@Param("startLat") Double startLat,
//			@Param("startLng") Double startLng,
//			@Param("destinationLat") Double destinationLat,
//			@Param("destinationLng") Double destinationLng,
//			@Param("startUpperLimit") Integer startUpperLimit,
//			@Param("destinationUpperLimit") Integer destinationUpperLimit
	);

	@Query(value = "SELECT * From fuvar WHERE statusz != 'TELJESITVE' AND statusz != 'NEM_TELJESITVE' AND indalasi_ido < ?1",
			nativeQuery = true)
	List<Fuvar> getWithElapsedIndulasiIdo(String dateTime); //'2020-10-19 17:12:31'

	@Query(value = "SELECT * From fuvar " +
			"WHERE statusz != 'TELJESITVE' " +
			"AND statusz != 'NEM_TELJESITVE' " +
			"AND lezarva_date IS NOT NULL " +
			"AND lezarva_date < ?1",
			nativeQuery = true)
	List<Fuvar> getWithElapsedFeedbackTime(String dateTime); //'2020-10-19 17:12:31'

	@Query(value = "SELECT fuvar.* " +
			"FROM fuvar " +
			"WHERE statusz = 'LEZARVA' " +
			"AND sofor_id = ?1 " +
			"AND fuvar.id NOT IN " +
			"(SELECT fuvar.id " +
			"FROM fuvar " +
			"JOIN feedback ON fuvar.id = feedback.fuvar_id " +
			"JOIN felhasznalo giver ON feedback.felhasznalo_id = giver.id " +
			"WHERE fuvar.sofor_id = ?1 " +
			"AND giver.id = ?1)",
			nativeQuery = true)
	List<Fuvar> getFuvarokToBeRatedAsSofor(Integer id);

	@Query(value =
			"SELECT fuvar.* " +
					"FROM fuvar " +
					"JOIN felhasznalo_fuvar on fuvar.id = felhasznalo_fuvar.fuvar_id " +
					"WHERE felhasznalo_fuvar.utas_id = ?1 " +
					"AND fuvar.statusz = 'LEZARVA' " +
					"AND fuvar.id NOT in " +
					"(select fuvar.id " +
					"from fuvar " +
					"JOIN feedback on fuvar.id = feedback.fuvar_id " +
					"JOIN felhasznalo giver on feedback.felhasznalo_id = giver.id " +
					"where giver.id = ?1)",
			nativeQuery = true)
	List<Fuvar> getFuvarokToBeRatedAsUtas(Integer id);

	@Query(value = "SELECT * " +
			"FROM fuvar " +
			"WHERE statusz = 'MEGHIRDETVE' AND sofor_id = ?",
			nativeQuery = true)
	List<Fuvar> getAktivFuvarokByFelhasznalo(Integer id);

	@Query(value = "SELECT * " +
			"FROM fuvar " +
			"WHERE statusz = 'MEGHIRDETVE' AND from_special_location = ?",
			nativeQuery = true)
	List<Fuvar> getAktivFuvarok(Boolean fromSpecialLocation);

	//TODO van capacity?
	@Query(value = "SELECT * " +
			"FROM fuvar " +
			"WHERE statusz = 'MEGHIRDETVE' AND sofor_id != ?1 " +
			"AND fuvar.id NOT IN (SELECT fuvar_id FROM `felhasznalo_fuvar` where utas_id = ?1)",
			nativeQuery = true)
	List<Fuvar> getAktivFuvarokByOther(Integer id);

	@Query(value = "SELECT * " +
			"FROM fuvar " +
			"JOIN felhasznalo_fuvar on fuvar.id = felhasznalo_fuvar.fuvar_id " +
			"WHERE felhasznalo_fuvar.utas_id = ?",
			nativeQuery = true)
	List<Fuvar> getAllByUtas(Integer utasId);

	Fuvar getFuvarById(Integer fuvarId);

	//TODO stored procedure?
	@Query(value = "SELECT count(fuvar.id) > 0 " +
			"FROM fuvar " +
			"JOIN felhasznalo_fuvar ON fuvar.id = felhasznalo_fuvar.fuvar_id " +
			"JOIN felhasznalo felh ON felhasznalo_fuvar.utas_id = felh.id " +
			"WHERE fuvar.id = ?1 AND felh.email = ?2",
			nativeQuery = true)
	Integer isPassenger(Integer fuvarId, String name);

	@Query(value = "SELECT count(fuvar.id) > 0 " +
			"FROM fuvar " +
			"JOIN felhasznalo felh ON fuvar.sofor_id = felh.id " +
			"WHERE fuvar.id = ?1 AND felh.email = ?2",
			nativeQuery = true)
	Integer isDriver(Integer fuvarId, String name);

	@Deprecated
	@Query(value = "SELECT * " +
			"FROM fuvar " +
			"WHERE statusz = 'MEGHIRDETVE' AND DATE(indalasi_ido) = ?1 AND uticel = ?2",
			nativeQuery = true)
	List<Fuvar> listByDateDestination(LocalDate date, String destination);

	@Deprecated
	@Query(value = "SELECT * " +
			"FROM fuvar " +
			"WHERE statusz = 'MEGHIRDETVE' AND uticel = ?",
			nativeQuery = true)
	List<Fuvar> listByDestination(String destination);

	@Query(value = "SELECT * " +
			"FROM fuvar " +
			"WHERE statusz = 'MEGHIRDETVE' AND DATE(indalasi_ido) LIKE ?1 AND uticel LIKE ?2",
			nativeQuery = true)
	List<Fuvar> listByDateAndDestination(String date, String destination);
}
