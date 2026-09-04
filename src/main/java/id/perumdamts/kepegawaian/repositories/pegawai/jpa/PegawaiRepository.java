package id.perumdamts.kepegawaian.repositories.pegawai.jpa;

import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiIdNipam;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.List;
import java.util.Optional;

public interface PegawaiRepository extends JpaRepository<Pegawai, Long>,
        JpaSpecificationExecutor<Pegawai>,
        RevisionRepository<Pegawai, Long, Integer> {

    Optional<Pegawai> findOneByNipam(String nipam);

    List<PegawaiIdNipam> findByStatusKerjaInAndStatusPegawai(List<EStatusKerja> dirumahkan, EStatusPegawai eStatusPegawai);

    boolean existsByJabatanId(Long id);

    /**
     * Scalar reads utk engine gaji (GajiBatchProsesReferenceResolver) — menghindari
     * lazy-load {@link Pegawai#getRumahDinas()} di luar transaksi kalkulasi.
     */
    @Query("select p.isAskes from Pegawai p where p.id = ?1")
    Optional<Boolean> findIsAskesById(Long pegawaiId);

    @Query("select rd.nilai from Pegawai p left join p.rumahDinas rd where p.id = ?1")
    Optional<Double> findRumahDinasNilaiById(Long pegawaiId);
}
