package id.perumdamts.kepegawaian.repositories.kepegawaian.jpa;

import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatMutasi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.time.LocalDate;

public interface RiwayatMutasiRepository extends JpaRepository<RiwayatMutasi, Long>,
RevisionRepository<RiwayatMutasi, Long, Integer> {
    boolean existsByRiwayatSk_NomorSkAndPegawai_IdAndRiwayatSk_TanggalSk(String nomorSk, Long pegawaiId, LocalDate tanggalSk);

    boolean existsByRiwayatSk_NomorSkAndPegawai_IdAndRiwayatSk_TanggalSkAndIdNot(String nomorSk, Long pegawaiId, LocalDate tanggalSk, Long id);
}
