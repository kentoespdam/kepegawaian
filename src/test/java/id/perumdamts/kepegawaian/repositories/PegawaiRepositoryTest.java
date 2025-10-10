package id.perumdamts.kepegawaian.repositories;

import id.perumdamts.kepegawaian.entities.pegawai.PegawaiProfilUpdate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest
@Slf4j
class PegawaiRepositoryTest {
    @Autowired
    private PegawaiRepository repository;

    @Test
    void findPegawaiProfilUpdate() {
        Optional<PegawaiProfilUpdate> pegawaiProfilUpdate = repository.findByNipam("900800456");
        assertTrue(pegawaiProfilUpdate.isPresent());
        log.info("{}", pegawaiProfilUpdate.get());
    }
}