package school.sorokin.eventnotificator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class EventnotificatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventnotificatorApplication.class, args);
	}

}
