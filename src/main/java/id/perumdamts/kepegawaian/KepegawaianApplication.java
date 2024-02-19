package id.perumdamts.kepegawaian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl",
		dateTimeProviderRef = "dateTimeProvider")
@SpringBootApplication
public class KepegawaianApplication {

	public static void main(String[] args) {
		SpringApplication.run(KepegawaianApplication.class, args);
	}

}
