package id.perumdamts.kepegawaian.repositories.profil.jpa;

import id.perumdamts.kepegawaian.entities.profil.PengalamanKerja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PengalamanKerjaRepository extends JpaRepository<PengalamanKerja, Long>, JpaSpecificationExecutor<PengalamanKerja> {
    /**
     * Carcass finder — bypasses {@code @SQLRestriction("is_deleted = FALSE")} so
     * DELETE-reject can reactivate a soft-deleted row (ADR-0036 §5).
     */
    @Query(value = "SELECT * FROM pengalaman_kerja WHERE id = ?1 LIMIT 1", nativeQuery = true)
    Optional<PengalamanKerja> findAnyById(Long id);
}
