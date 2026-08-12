package id.perumdamts.kepegawaian.repositories.profil.jpa;

import id.perumdamts.kepegawaian.entities.profil.Biodata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface BiodataRepository extends
        JpaRepository<Biodata, String>,
        JpaSpecificationExecutor<Biodata>,
        QueryByExampleExecutor<Biodata>,
        RevisionRepository<Biodata, String, Long> {
    /**
     * Carcass finder — bypasses {@code @SQLRestriction("is_deleted = FALSE")} so
     * DELETE-reject can reactivate a soft-deleted row (ADR-0036 §5).
     */
    @Query(value = "SELECT * FROM biodata WHERE nik = ?1 LIMIT 1", nativeQuery = true)
    java.util.Optional<Biodata> findAnyByNik(String nik);
}
