package id.perumdamts.kepegawaian.repositories.master.jpa;

import id.perumdamts.kepegawaian.entities.master.Organisasi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

public interface OrganisasiRepository extends JpaRepository<Organisasi, Long>,
        JpaSpecificationExecutor<Organisasi>,
        RevisionRepository<Organisasi, Long, Integer> {

    boolean existsByParentIdAndIsDeletedFalse(Long parentId);

    /**

    /**
     * Native carcass-finder — bypasses {@code @SQLRestriction("is_deleted = FALSE")}
     * on {@code MasterBaseEntity} so the create-seam can see soft-deleted rows and
     * revive them (ADR-0005). Key is {@code nama} + {@code parent_id}
     * (per {@code kepegawaian-jow} decision 2026-06-18).
     *
     * Caller must distinguish active vs deleted (the revive branch in
     * {@code OrganisasiCommandService.create()} does this).
     */
    @Query(value = "SELECT * FROM organisasi " +
            "WHERE nama = ?1 " +
            "AND (parent_id = ?2 OR (?2 IS NULL AND parent_id IS NULL)) " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<Organisasi> findAnyByUniqueKey(String nama, Long parentId);
}
