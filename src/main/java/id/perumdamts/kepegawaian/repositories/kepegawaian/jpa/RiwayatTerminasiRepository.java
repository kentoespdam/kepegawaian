package id.perumdamts.kepegawaian.repositories.kepegawaian.jpa;

import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatTerminasi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.time.LocalDate;

public interface RiwayatTerminasiRepository extends JpaRepository<RiwayatTerminasi, Long>,
RevisionRepository<RiwayatTerminasi, Long, Integer> {
    boolean existsByPegawai_IdAndSkTerminasi_NomorSkAndSkTerminasi_TanggalSk(Long pegawaiId, String nomorSk, LocalDate tanggalSk);

    boolean existsByPegawai_IdAndSkTerminasi_NomorSkAndSkTerminasi_TanggalSkAndIdNot(Long pegawaiId, String nomorSk, LocalDate tanggalSk, Long id);
}
