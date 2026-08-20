package id.perumdamts.kepegawaian.config;

import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@ConfigurationProperties(prefix = "app.pegawai")
public record PegawaiProperties(
        Set<Long> excludedJabatanIds,
        Set<EStatusPegawai> excludedGolonganStatuses
) {}
