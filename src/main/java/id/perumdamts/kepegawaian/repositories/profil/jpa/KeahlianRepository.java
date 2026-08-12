package id.perumdamts.kepegawaian.repositories.profil.jpa;

import id.perumdamts.kepegawaian.entities.profil.Keahlian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

public interface KeahlianRepository extends JpaRepository<Keahlian, Long>,
        JpaSpecificationExecutor<Keahlian>,
        RevisionRepository<Keahlian, Long, Integer> {
    /**
     * Carcass finder — bypasses {@code @SQLRestriction("is_deleted = FALSE")} so
     * DELETE-reject can reactivate a soft-deleted row (ADR-0036 §5).
     */
    @Query(value = "SELECT * FROM keahlian WHERE id = ?1 LIMIT 1", nativeQuery = true)
    Optional<Keahlian> findAnyById(Long id);
}
