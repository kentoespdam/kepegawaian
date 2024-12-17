package id.perumdamts.kepegawaian.repositories.penggajian;

import id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen.GajiKomponenMiniProjection;
import id.perumdamts.kepegawaian.entities.penggajian.GajiKomponen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GajiKomponenRepository extends JpaRepository<GajiKomponen, Long>,
        JpaSpecificationExecutor<GajiKomponen>,
        RevisionRepository<GajiKomponen, Long, Long> {

    @Query("SELECT DISTINCT gk.kode AS kode, gk.nama AS nama " +
            "FROM GajiKomponen gk " +
            "WHERE gk.profilGaji.id = :profilId")
    List<GajiKomponenMiniProjection> findAllKode(@Param("profilId") Long profilId);

    @Query("SELECT gk.urut FROM GajiKomponen gk WHERE gk.profilGaji.id = :profilId ORDER BY gk.urut DESC LIMIT 1")
    Integer findLastUrut(Long profilId);
}
