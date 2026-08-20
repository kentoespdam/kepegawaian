package id.perumdamts.kepegawaian.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.cuti")
public record CutiProperties(
        Long jenisCutiTahunan,
        Long jenisCutiBesar,
        Long jenisCutiIbadah,
        Long supervisorSdm,
        Long managerSdm,
        Long direkturUtama,
        Long direkturUmum,
        Long levelSupervisor,
        Long levelManager
) {}
