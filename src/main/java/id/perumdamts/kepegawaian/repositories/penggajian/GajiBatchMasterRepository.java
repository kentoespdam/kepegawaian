package id.perumdamts.kepegawaian.repositories.penggajian;

import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GajiBatchMasterRepository extends JpaRepository<GajiBatchMaster, Long>,
        JpaSpecificationExecutor<GajiBatchMaster> {
}
