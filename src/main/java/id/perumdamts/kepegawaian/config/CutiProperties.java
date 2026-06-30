package id.perumdamts.kepegawaian.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.cuti")
public class CutiProperties {
    private Long jenisCutiTahunan = 1L;
    private Long jenisCutiBesar = 2L;
    private Long jenisCutiIbadah = 4L;
    private Long supervisorSdm = 49L;
    private Long managerSdm = 48L;
    private Long direkturUtama = 2L;
    private Long direkturUmum = 25L;
    private Long levelSupervisor = 6L;
    private Long levelManager = 5L;
}
