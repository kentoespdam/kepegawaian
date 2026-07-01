package id.perumdamts.kepegawaian.repositories.pegawai.jpa;

import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiIdNipam;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.List;
import java.util.Optional;

public interface PegawaiRepository extends JpaRepository<Pegawai, Long>,
        JpaSpecificationExecutor<Pegawai>,
        RevisionRepository<Pegawai, Long, Integer> {

    Optional<Pegawai> findOneByNipam(String nipam);

    List<PegawaiIdNipam> findByStatusKerjaInAndStatusPegawai(List<EStatusKerja> dirumahkan, EStatusPegawai eStatusPegawai);

    boolean existsByJabatanId(Long id);
}
