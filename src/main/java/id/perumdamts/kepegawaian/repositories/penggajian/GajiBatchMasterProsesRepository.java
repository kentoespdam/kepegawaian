package id.perumdamts.kepegawaian.repositories.penggajian;

import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMasterProses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GajiBatchMasterProsesRepository extends JpaRepository<GajiBatchMasterProses, Long>,
        JpaSpecificationExecutor<GajiBatchMasterProses> {
}
