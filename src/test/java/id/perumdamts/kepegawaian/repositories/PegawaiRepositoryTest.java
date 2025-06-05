package id.perumdamts.kepegawaian.repositories;

import id.perumdamts.kepegawaian.dto.pegawai.PegawaiIdNipam;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class PegawaiRepositoryTest {
    @Autowired
    private PegawaiRepository repository;

    @Test
    void findListIdAndNipam() {
        List<PegawaiIdNipam> listIdAndNipam = repository.findByStatusKerjaInAndStatusPegawai(List.of(EStatusKerja.DIRUMAHKAN, EStatusKerja.KARYAWAN_AKTIF), EStatusPegawai.PEGAWAI);
        listIdAndNipam.forEach(pegawaiIdNipam -> log.info("id: {}, nipam: {}", pegawaiIdNipam.getId(), pegawaiIdNipam.getNipam()));

    }
}