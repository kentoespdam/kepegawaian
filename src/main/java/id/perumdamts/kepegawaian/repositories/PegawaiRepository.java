package id.perumdamts.kepegawaian.repositories;

import id.perumdamts.kepegawaian.dto.pegawai.PegawaiIdNipam;
import id.perumdamts.kepegawaian.entities.commons.EStatusKerja;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PegawaiRepository extends JpaRepository<Pegawai, Long>,
        JpaSpecificationExecutor<Pegawai>,
        RevisionRepository<Pegawai, Long, Long> {
    Optional<Pegawai> findByBiodata_Nik(String nik);

    Optional<Pegawai> findOneByNipam(String nipam);

//    @Query("SELECT p.id, p.nipam FROM Pegawai p WHERE p.isDeleted = false AND p.statusKerja IN (:statusKerjaList) AND p.statusPegawai = :statusPegawai ORDER BY p.nipam ASC")
    List<PegawaiIdNipam> findByStatusKerjaInAndStatusPegawai(@Param("statusKerjaList") List<EStatusKerja> statusKerjaList, @Param("statusPegawai") EStatusPegawai statusPegawai);
}
