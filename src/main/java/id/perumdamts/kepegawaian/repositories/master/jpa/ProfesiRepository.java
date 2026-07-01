package id.perumdamts.kepegawaian.repositories.master.jpa;

import id.perumdamts.kepegawaian.entities.master.Profesi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

public interface ProfesiRepository extends JpaRepository<Profesi, Long>,
        JpaSpecificationExecutor<Profesi>,
        RevisionRepository<Profesi, Long, Integer> {
    /**
     * Eagerly fetches a live Profesi row matching the (nama, jabatanId, gradeId) tuple.
     * Explicit JPQL avoids any future {@code @SQLRestriction} side effects.
     */
    Optional<Profesi> findFirstByNamaAndJabatan_IdAndGrade_IdAndIsDeletedFalse(
            String nama, Long jabatanId, Long gradeId);

    /**
     * Eagerly fetches an archived Profesi row matching the (nama, jabatanId, gradeId) tuple.
     * Bypasses {@code @SQLRestriction("is_deleted = FALSE")} via Spring Data method-name JPQL,
     * enabling revive-on-create and edit-rejection-across-archive.
     */
    Optional<Profesi> findFirstByNamaAndJabatan_IdAndGrade_IdAndIsDeletedTrue(
            String nama, Long jabatanId, Long gradeId);
}
