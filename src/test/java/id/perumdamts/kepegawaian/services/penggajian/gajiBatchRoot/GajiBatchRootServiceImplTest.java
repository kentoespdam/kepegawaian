package id.perumdamts.kepegawaian.services.penggajian.gajiBatchRoot;

import id.perumdamts.kepegawaian.entities.commons.EJenisPotonganGaji;
import id.perumdamts.kepegawaian.entities.commons.EProsesGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRootLampiran;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchRootRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class GajiBatchRootServiceImplTest {
    @Autowired
    private GajiBatchRootRepository repository;

    //    @Test
    void simpan() {
        GajiBatchRoot gajiBatchRoot = new GajiBatchRoot();
        gajiBatchRoot.setId("202401-001");
        gajiBatchRoot.setPeriode("202401");
        gajiBatchRoot.setStatus(EProsesGaji.PENDING.value());
        gajiBatchRoot.setTanggalProses(LocalDateTime.now());
        gajiBatchRoot.setDiProsesOleh("test");
        gajiBatchRoot.setJabatanPemroses("test");

        log.info("entity: {}", gajiBatchRoot);

        GajiBatchRootLampiran gajiBatchRootLampiran = new GajiBatchRootLampiran();
        gajiBatchRootLampiran.setGajiBatchRoot(gajiBatchRoot);
        gajiBatchRootLampiran.setJenisLampiranGaji(EJenisPotonganGaji.POTONGAN_TKK);
        gajiBatchRootLampiran.setFileName("test.xlsx");
        gajiBatchRootLampiran.setMimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        gajiBatchRootLampiran.setHashedFileName("randomStringInHere");

        log.info("entity: {}", gajiBatchRootLampiran);
    }

    @Test
    void getByBatchId() {
        GajiBatchRoot gajiBatchRoot = repository.findById("202401-001").orElse(null);
        assertNotNull(gajiBatchRoot);
        log.info("entity: {}", gajiBatchRoot);
    }
}