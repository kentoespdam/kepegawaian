package id.perumdamts.kepegawaian.services.setupMaster;

import id.perumdamts.kepegawaian.entities.master.AlasanBerhenti;
import id.perumdamts.kepegawaian.repositories.master.AlasanBerhentiRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@Slf4j
class SetupMasterTest {
    @Autowired
    private AlasanBerhentiRepository repository;

    @Test
    @Transactional
    void setupAlasanBerhenti() {
//        List<AlasanBerhenti> list = List.of(
//                new AlasanBerhenti(1L, "Mengundurkan Diri", ""),
//                new AlasanBerhenti(2L, "Diberhentikan", ""),
//                new AlasanBerhenti(3L, "Kontrak Berakhir", ""),
//                new AlasanBerhenti(4L, "Pensiun Normal", ""),
//                new AlasanBerhenti(5L, "Meninggal Dunia", "")
//        );
//        list.forEach(item -> {
//            repository.saveAndFlush(item);
//        });
        repository.saveAndFlush(new AlasanBerhenti("Mengundurkan Diri", ""));
    }
}