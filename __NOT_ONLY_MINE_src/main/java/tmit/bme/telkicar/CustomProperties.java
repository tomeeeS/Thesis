package tmit.bme.telkicar;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "emailservice")
@Getter
@Setter
public class CustomProperties {
	private Boolean enabled = true;
}
