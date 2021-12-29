package tmit.bme.telkicar.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import tmit.bme.telkicar.domain.advert.Igeny;

import java.time.LocalDateTime;
import java.util.List;

public interface IgenyRepository extends CrudRepository<Igeny, Integer> {

    @Query(value = "SELECT * " +
        "FROM igeny " +
//			"JOIN location departure on igeny.departure_id = departure.id " +
        "JOIN location destination on igeny.destination_id = destination.id " +
//			"WHERE " +
//			"  (:date is null OR DATE(igeny.indalasi_ido) = DATE(:date)) " +
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
        "ORDER BY ABS(TIMEDIFF(igeny.indalasi_ido, :date)), " +
        "         ST_Distance_Sphere( " +
        "                 point(destination.lat, destination.lng), " +
        "                 point(:destinationLat, :destinationLng) " +
        "             )"
        ,
        nativeQuery = true)
    List<Igeny> search(
        @Param("date") LocalDateTime date
//			@Param("startLat") Double startLat,
//			@Param("startLng") Double startLng,
//			@Param("destinationLat") Double destinationLat,
//			@Param("destinationLng") Double destinationLng,
//			@Param("startUpperLimit") Integer startUpperLimit,
//			@Param("destinationUpperLimit") Integer destinationUpperLimit
    );

    @Query(value = "SELECT * " +
        "FROM igeny " +
        "WHERE DATE(indalasi_ido) LIKE ?1 AND uticel LIKE ?2",
        nativeQuery = true)
    List<Igeny> listByDateDestination(String date, String destination);

    @Query(value = "SELECT * " +
        "FROM igeny " +
        "WHERE hirdeto_id = ?1",
        nativeQuery = true)
    List<Igeny> getAktivIgenyByFelhasznalo(Integer hirdetoId);

    @Query(value = "SELECT * " +
        "FROM igeny " +
        "WHERE from_special_location = ?",
        nativeQuery = true)
    List<Igeny> getAktivIgenyek(Boolean fromSpecialLocation);
}
