package id.perumdamts.kepegawaian.services.setupMaster;

import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiKomponen;
import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class SetupGajiKomponenTest {
    @Test
    void test() {
        GajiKomponen gajiKomponen = new GajiKomponen(19L, 19, new GajiProfil(1L), "POT_PENSIUN", "Iuran Pensiun", EJenisGaji.POTONGAN, 0, false, "PHDP * 0.05");
        log.info("gaji komponen: {}", gajiKomponen);
    }
}