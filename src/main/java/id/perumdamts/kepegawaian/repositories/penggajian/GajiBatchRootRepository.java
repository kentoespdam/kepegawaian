package id.perumdamts.kepegawaian.repositories.penggajian;

import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GajiBatchRootRepository extends JpaRepository<GajiBatchRoot, String>,
        JpaSpecificationExecutor<GajiBatchRoot> {
    @Query(
            value = "SELECT DISTINCT * FROM gaji_batch_root gbr WHERE gbr.periode = :periode AND gbr.is_deleted = TRUE ORDER BY gbr.batch_id DESC LIMIT 1",
            nativeQuery = true
    )
    Optional<GajiBatchRoot> findDeletedBatchRoot(String periode);

    Optional<GajiBatchRoot> findByPeriode(String periode);
}
