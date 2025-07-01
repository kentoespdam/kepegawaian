package id.perumdamts.kepegawaian.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class DefConfig {
    @Value("${custom.protected.delete.kartuIdentitas.ktp}")
    private Long PROTECTED_KARTU_IDENTITAS_ID;
    @Value("${custom.jenisCuti.tahunan}")
    private Long jenisCutiTahunan;
    @Value("${custom.jenisCuti.besar}")
    private Long jenisCutiBesar;
    @Value("${custom.jenisCuti.ibadah}")
    private Long jenisCutiIbadah;
}
