package id.perumdamts.kepegawaian.repositories.penggajian.jpa;

import id.perumdamts.kepegawaian.entities.penggajian.GajiKpi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

public interface GajiKpiRepository extends JpaRepository<GajiKpi, Long>,
        JpaSpecificationExecutor<GajiKpi>,
        RevisionRepository<GajiKpi, Long, Integer> {
    Optional<GajiKpi> findByNipamAndPeriode(String nipam, String periode);

    /**
     * Native carcass-finder — bypasses {@code @SQLRestriction("is_deleted = FALSE")}
     * on {@code GajiKpi} so the create-seam can see soft-deleted rows and revive
     * them (pola kepegawaian-33s / ADR-0005). Key is {@code nipam} + {@code periode}
     * (unique {@code uk_gj_kpi_nipam_periode}).
     *
     * Caller must distinguish active vs deleted (the revive branch in
     * {@code GajiKpiCommandService.save()} does this).
     */
    @Query(value = "SELECT * FROM gaji_kpi " +
            "WHERE nipam = ?1 AND periode = ?2 " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<GajiKpi> findAnyByNipamAndPeriode(String nipam, String periode);
}
