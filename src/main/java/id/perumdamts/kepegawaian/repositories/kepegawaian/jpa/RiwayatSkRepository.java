package id.perumdamts.kepegawaian.repositories.kepegawaian.jpa;

import id.perumdamts.kepegawaian.entities.commons.EJenisSk;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RiwayatSkRepository extends JpaRepository<RiwayatSk, Long> {
    List<RiwayatSk> findByIdIn(List<Long> riwayatIds);

    List<RiwayatSk> findByPegawai_Id(Long pegawaiId);

    List<RiwayatSk> findByPegawai_IdOrderByTmtBerlakuDesc(Long id);

    boolean existsByPegawai_IdAndNomorSkAndJenisSkAndTanggalSk(Long pegawaiId, String nomorSk, EJenisSk jenisSk, LocalDate tanggalSk);

    boolean existsByPegawai_IdAndNomorSkAndJenisSkAndTanggalSkAndIdNot(Long pegawaiId, String nomorSk, EJenisSk jenisSk, LocalDate tanggalSk, Long id);
}
