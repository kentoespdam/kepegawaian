package id.perumdamts.kepegawaian.repositories.profil.jpa;

import id.perumdamts.kepegawaian.entities.profil.KartuIdentitas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.Optional;

public interface KartuIdentitasRepository extends
        JpaRepository<KartuIdentitas, Long>,
        JpaSpecificationExecutor<KartuIdentitas>,
        RevisionRepository<KartuIdentitas, Long, Integer>,
        QueryByExampleExecutor<KartuIdentitas> {
    /**
     * Native carcass-finder — bypasses {@code @SQLRestriction("is_deleted = FALSE")}
     * on {@code KartuIdentitas} so the create-seam can see soft-deleted rows and
     * revive them (ADR-0005, kepegawaian-mee.4). Natural key is
     * {@code nik + jenis_kitas_id + nomor_kartu} (unique constraint includes
     * is_deleted so soft-deleted duplicates coexist until revived).
     */
    @Query(value = "SELECT * FROM kartu_identitas " +
            "WHERE nik = ?1 AND jenis_kitas_id = ?2 AND nomor_kartu = ?3 " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<KartuIdentitas> findAnyByUniqueKey(String nik, Long jenisKitasId, String nomorKartu);

    /**
     * Carcass finder — bypasses {@code @SQLRestriction("is_deleted = FALSE")} so
     * DELETE-reject can reactivate a soft-deleted row (ADR-0036 §5).
     */
    @Query(value = "SELECT * FROM kartu_identitas WHERE id = ?1 LIMIT 1", nativeQuery = true)
    Optional<KartuIdentitas> findAnyById(Long id);
}
