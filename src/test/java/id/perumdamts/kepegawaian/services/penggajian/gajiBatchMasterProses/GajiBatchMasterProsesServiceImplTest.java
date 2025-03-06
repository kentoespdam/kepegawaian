package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses;

import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMasterProses;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchMasterProsesRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchMasterRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@Slf4j
@ActiveProfiles("test")
class GajiBatchMasterProsesServiceImplTest {
    @Autowired
    private GajiBatchMasterProsesRepository repository;
    @Autowired
    private GajiBatchMasterRepository batchMasterRepository;

    @Test
//    @Transactional
    void test() {
        List<GajiBatchMasterProses> byGajiBatchMasterId = repository.findByGajiBatchMaster_Id(10294L);
        assertNotEquals(0, byGajiBatchMasterId.size());

        double addPemasukan = getAdditional(byGajiBatchMasterId, EJenisGaji.PEMASUKAN);
        double addPotongan = getAdditional(byGajiBatchMasterId, EJenisGaji.POTONGAN);

        double penghasilanKotor = byGajiBatchMasterId.stream().filter(gbp -> gbp.getKode().equals("PENGHASILAN_KOTOR"))
                .mapToDouble(GajiBatchMasterProses::getNilai).sum();
        double potongan = byGajiBatchMasterId.stream().filter(gbp -> gbp.getJenisGaji().equals(EJenisGaji.POTONGAN))
                .mapToDouble(GajiBatchMasterProses::getNilai).sum() + addPotongan;
        double penghasilanBersih = penghasilanKotor - potongan;
        updateGajiBatchMaster(penghasilanBersih, addPemasukan, addPotongan);

//        batchMasterRepository.save(gajiBatchMaster);
    }

    private void updateGajiBatchMaster(double penghasilanBersih, double addPemasukan, double addPotongan) {
        double pembulatan = Math.ceil(penghasilanBersih / 100) * 100 - penghasilanBersih;
        double penghasilanBersihFinal = penghasilanBersih + pembulatan;

        batchMasterRepository.findById(10294L).ifPresent(gbm -> {
            gbm.setTotalAddTambahan(addPemasukan);
            gbm.setTotalAddPotongan(addPotongan);
            gbm.setPenghasilanBersih2(penghasilanBersih);
            gbm.setPembulatan2(pembulatan);
            gbm.setPenghasilanBersihFinal2(penghasilanBersihFinal);
            log.info("gbm: {}", gbm.getRootBatchId());
//            batchMasterRepository.save(gbm);
        });
    }

    private double getAdditional(List<GajiBatchMasterProses> processes, EJenisGaji gajiType) {
        return processes.stream()
                .filter(process -> process.getJenisGaji().equals(gajiType) && process.getKode().startsWith("ADD_"))
                .mapToDouble(GajiBatchMasterProses::getNilai)
                .sum();
    }
}