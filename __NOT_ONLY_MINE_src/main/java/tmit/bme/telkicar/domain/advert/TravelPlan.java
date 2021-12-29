package tmit.bme.telkicar.domain.advert;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cascade;
import tmit.bme.telkicar.logic.geography.GeoPoint;
import tmit.bme.telkicar.logic.geography.roadnetwork.RoadNode;
import tmit.bme.telkicar.logic.matching.ActionPoint;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@MappedSuperclass
public abstract class TravelPlan implements ActionPoint { // old name: Advert

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Integer id;
	@Column(name = "indalasi_ido")
	protected LocalDateTime indulasiIdo;
	@Column(name = "indulasi_hely")
	protected String indulasiHely;
	@Column(name = "uticel")
	protected String uticel;
	@Column(name = "info")
	private String info;
	@Column(name = "road_graph_departure_node_id")
	protected Long roadGraphStartNodeId;
	@Column(name = "road_graph_destination_node_id")
	protected Long roadGraphDestinationNodeId;

	@Column(name = "from_special_location")
	@Builder.Default()
	// true <=> departure is the special location, false <=> destination is the special location
	private Boolean fromSpecialLocation = true;

	@OneToOne
	@JoinColumn(name = "destination_id", referencedColumnName = "id")
	@Cascade(org.hibernate.annotations.CascadeType.DELETE)
	protected Location destination;
	@OneToOne
	@JoinColumn(name = "departure_id", referencedColumnName = "id")
	@Cascade(org.hibernate.annotations.CascadeType.DELETE)
	protected Location departure;

    public String getIndulasiIdoString() {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY.MM.dd HH:mm");
		return indulasiIdo.format(dateTimeFormatter);
	}

	public Integer getId() {
		return id;
	}

	public GeoPoint getDepartureGeoPoint() {
		return new GeoPoint(departure.getLat(), departure.getLng());
	}

	public GeoPoint getDestinationGeoPoint() {
		return new GeoPoint(destination.getLat(), destination.getLng());
	}

	public RoadNode getStartRoadNode() {
		return new RoadNode(
			roadGraphStartNodeId,
			getDepartureGeoPoint()
		);
	}

	public RoadNode getDestinationRoadNode() {
		return new RoadNode(
			roadGraphDestinationNodeId,
			getDestinationGeoPoint()
		);
	}

	public GeoPoint getDefiningEndpointGeoPoint() {
    	if (fromSpecialLocation)
			return getDestinationGeoPoint();
    	else
			return getDepartureGeoPoint();
	}

	public RoadNode getDefiningEndpointRoadNode() {
    	if (fromSpecialLocation)
			return getDestinationRoadNode();
    	else
			return getStartRoadNode();
	}

}
