package id.perumdamts.kepegawaian.repositories.pegawai.jpa;

import id.perumdamts.kepegawaian.dto.pegawai.pegawai.PegawaiIdNipam;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PegawaiRepository extends JpaRepository<Pegawai, Long>,
        JpaSpecificationExecutor<Pegawai>,
        RevisionRepository<Pegawai, Long, Integer> {

    Optional<Pegawai> findOneByNipam(String nipam);

    /**
     * Pegawai eligible engine gaji (keputusan #8): statusKerja KARYAWAN_AKTIF,
     * statusPegawai != NON_PEGAWAI. Fetch joins eager utk snapshot Wave 5:
     * biodata (statusKawin), jabatan+level (levelId), golongan, gajiProfil,
     * kodePajak. Organisasi & biodata EAGER di entity; sisanya join eksplisit.
     */
    @Query("""
            select p from Pegawai p
            join fetch p.biodata
            left join fetch p.jabatan j
            left join fetch j.level
            left join fetch p.golongan
            left join fetch p.gajiProfil
            left join fetch p.kodePajak
            where p.statusKerja = ?1 and p.statusPegawai <> ?2
            """)
    List<Pegawai> findEligibleForGaji(EStatusKerja statusKerja, EStatusPegawai statusPegawai);

    List<PegawaiIdNipam> findByStatusKerjaInAndStatusPegawai(List<EStatusKerja> dirumahkan, EStatusPegawai eStatusPegawai);

    boolean existsByJabatanId(Long id);

    /**
     * Scalar reads utk engine gaji (GajiPreloadService) — menghindari
     * lazy-load {@link Pegawai#getRumahDinas()} di luar transaksi kalkulasi.
     */
    @Query("select p.isAskes from Pegawai p where p.id = ?1")
    Optional<Boolean> findIsAskesById(Long pegawaiId);

    @Query("select rd.nilai from Pegawai p left join p.rumahDinas rd where p.id = ?1")
    Optional<Double> findRumahDinasNilaiById(Long pegawaiId);

    @Query("select p.id, p.isAskes from Pegawai p where p.id in ?1")
    List<Object[]> findIsAskesByIdIn(Collection<Long> pegawaiIds);

    @Query("select p.id, rd.nilai from Pegawai p left join p.rumahDinas rd where p.id in ?1")
    List<Object[]> findRumahDinasNilaiByIdIn(Collection<Long> pegawaiIds);
}
