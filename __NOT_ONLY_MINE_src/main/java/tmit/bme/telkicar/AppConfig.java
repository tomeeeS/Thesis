package tmit.bme.telkicar;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.postgis.PGgeometry;
import org.postgresql.PGConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import tmit.bme.telkicar.logic.geography.distance.DistanceMetric;
import tmit.bme.telkicar.logic.geography.distance.HaversineCalculator;
import tmit.bme.telkicar.logic.geography.distance.HaversineDistanceMetric;
import tmit.bme.telkicar.logic.geography.distance.VincentyDistanceMetric;
import tmit.bme.telkicar.logic.geography.roadnetwork.RoadNetwork;
import tmit.bme.telkicar.logic.helpers.AppContextHelper;
import tmit.bme.telkicar.logic.helpers.NycAppContextHelper;
import tmit.bme.telkicar.logic.helpers.TelkiAppContextHelper;
import tmit.bme.telkicar.logic.matching.ParticipantMatcher;
import tmit.bme.telkicar.logic.matching.ip.IntegerProgrammingMatcher;
import tmit.bme.telkicar.logic.matching.ip.IpSolver;
import tmit.bme.telkicar.logic.matching.ip.LpSolveIpSolver;
import tmit.bme.telkicar.logic.repository.SpatialRepository;
import tmit.bme.telkicar.logic.routing.routeplanner.pointtopoint.AcRoutePlanner;
import tmit.bme.telkicar.logic.routing.routeplanner.pointtopoint.RoutePlanner;
import tmit.bme.telkicar.service.FuvarService;
import tmit.bme.telkicar.service.IgenyService;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {

	@Autowired
	private Environment env;

	@Autowired
	private FuvarService fuvarService;
	@Autowired
	private IgenyService igenyService;

	private final Logger logger = LoggerFactory.getLogger(getClass().getName());

	// use TelkiAppContextHelper for Telki, NycAppContextHelper for New York
	@Bean
	public AppContextHelper getAppContextHelper(@Autowired ApplicationContext applicationContext) {
		if (Boolean.parseBoolean(env.getProperty("isTelki")))
			return new TelkiAppContextHelper(applicationContext);
		else
			return new NycAppContextHelper(applicationContext);
	}

	@Autowired
	public DataSource mainDataBase() {
		ComboPooledDataSource securityDataSource = new ComboPooledDataSource();

		try {
			securityDataSource.setDriverClass(env.getProperty("spring.datasource.driver-class-name"));
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}

		logger.info(">>> jdbc.url: " + env.getProperty("spring.datasource.url"));

		securityDataSource.setJdbcUrl(env.getProperty("spring.datasource.url"));
		securityDataSource.setUser(env.getProperty("spring.datasource.username"));
		securityDataSource.setPassword(env.getProperty("spring.datasource.password"));

		securityDataSource.setInitialPoolSize(getIntProperty("connection.pool.initialPoolSize"));
		securityDataSource.setMinPoolSize(getIntProperty("connection.pool.minPoolSize"));
		securityDataSource.setMaxPoolSize(getIntProperty("connection.pool.maxPoolSize"));
		securityDataSource.setMaxIdleTime(getIntProperty("connection.pool.maxIdleTime"));

		return securityDataSource;
	}


	@Bean
	public Connection spatialDataSource() throws SQLException {
//		Class.forName(env.getProperty("spatialDb.datasource.driver-class-name"));
		String urlKey;
		if (Boolean.parseBoolean(env.getProperty("isTelki")))
			urlKey = "spatialDb.datasource.urlTelki";
		else
			urlKey = "spatialDb.datasource.urlNyc";
		String url = env.getProperty(urlKey);
		Connection connection = DriverManager.getConnection(
			url,
			env.getProperty("spatialDb.datasource.username"),
			env.getProperty("spatialDb.datasource.password")
		);

		logger.info(">>> postgresql jdbc.url: " + url);

		((PGConnection)connection).addDataType("geometry", PGgeometry.class);
		return connection;
	}

	@Autowired
	private RoadNetwork roadNetwork;

	@Autowired
	private SpatialRepository spatialRepository;

	@Bean(name = "routePlanner")
	@Scope("prototype") // new instance every time
	public RoutePlanner routePlanner(@Autowired AppContextHelper appContextHelper) {
		return new AcRoutePlanner(appContextHelper, roadNetwork);
	}

	@Bean(name = "matcher")
	@Scope("prototype") // new instance every time
	public ParticipantMatcher participantMatcher(@Autowired IpSolver ipSolver) {
		return new IntegerProgrammingMatcher(spatialRepository, ipSolver);
	}

	@Bean(name = "ipSolver")
	@Scope("prototype") // new instance every time
	public IpSolver ipSolver() {
		return new LpSolveIpSolver();
	}

	// for graph building, routing heuristic
	@Bean(name = "accurateDistance")
	public DistanceMetric accurateDistanceMetric() {
		return new VincentyDistanceMetric();
	}

	// faster to compute. used for map matching
	@Bean(name = "fastDistance")
	public DistanceMetric fastDistanceMetric() {
		return new HaversineDistanceMetric(new HaversineCalculator());
	}



	private int getIntProperty(String propertyName) {
		return Integer.parseInt(env.getProperty(propertyName));
	}

}