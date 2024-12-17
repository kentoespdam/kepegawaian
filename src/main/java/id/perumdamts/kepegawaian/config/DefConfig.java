package id.perumdamts.kepegawaian.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefConfig {
    @Value("${custom.protected.delete.kartuIdentitas.ktp}")
    private Long PROTECTED_KARTU_IDENTITAS_ID;
}
