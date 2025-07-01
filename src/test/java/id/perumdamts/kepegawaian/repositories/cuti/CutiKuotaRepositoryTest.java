package id.perumdamts.kepegawaian.repositories.cuti;

import id.perumdamts.kepegawaian.config.DefConfig;
import id.perumdamts.kepegawaian.entities.cuti.CutiKuota;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class CutiKuotaRepositoryTest {
    @Autowired
    private CutiKuotaRepository repository;

    @Autowired
    private DefConfig defConfig;

//    @Value("${custom.jenisCuti.besar}")
//    private Long jenisCutiBesar;
//    @Value("${custom.jenisCuti.ibadah}")
//    private Long jenisCutiIbadah;

    @Test
    void findByPegawai_IdAndTahun() {
        Optional<CutiKuota> oneByPegawaiIdAndTahun = repository.findByPegawai_IdAndTahun(483L, 2025);
        if (oneByPegawaiIdAndTahun.isEmpty())
            log.info("empty");
        else
            log.info("tahun: {}", oneByPegawaiIdAndTahun.get().getTahun());
    }

    @Test
    void getJenisCutiId(){
        log.info("jenis cuti besar: {}", defConfig.getJenisCutiBesar());
        log.info("jenis cuti ibadah: {}", defConfig.getJenisCutiIbadah());
    }
}