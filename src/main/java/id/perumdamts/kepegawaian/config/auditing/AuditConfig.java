package id.perumdamts.kepegawaian.config.auditing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;

import java.time.LocalDateTime;
import java.util.Optional;

@Configuration
public class AuditConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditAwareImpl();
    }

    @Bean
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(LocalDateTime.now());
    }
}
