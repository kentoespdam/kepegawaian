package id.perumdamts.kepegawaian.repositories.kepegawaian.jpa;

import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatKontrak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import id.perumdamts.kepegawaian.entities.commons.EJenisKontrak;

import java.util.List;

public interface RiwayatKontrakRepository extends JpaRepository<RiwayatKontrak, Long>,
RevisionRepository<RiwayatKontrak, Long, Integer> {
    boolean existsByPegawai_IdAndNipamAndNomorKontrakAndJenisKontrak(Long pegawaiId, String nipam, String nomorKontrak, EJenisKontrak jenisKontrak);

    List<RiwayatKontrak> findByPegawai_IdAndIdNot(Long pegawaiId, Long id);
}
