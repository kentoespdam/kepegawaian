package id.perumdamts.kepegawaian.config;

import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Data
@Component
@ConfigurationProperties(prefix = "app.pegawai")
public class PegawaiProperties {
    private Set<Long> excludedJabatanIds = new HashSet<>(Set.of(1L, 2L, 3L, 25L));
    private Set<EStatusPegawai> excludedGolonganStatuses = new HashSet<>(Set.of(
            EStatusPegawai.KONTRAK,
            EStatusPegawai.CALON_HONORER,
            EStatusPegawai.HONORER
    ));
}
