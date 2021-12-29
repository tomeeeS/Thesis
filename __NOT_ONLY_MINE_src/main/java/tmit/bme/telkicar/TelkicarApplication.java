package tmit.bme.telkicar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan("tmit.bme.telkicar")
public class TelkicarApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelkicarApplication.class, args);
	}

}
