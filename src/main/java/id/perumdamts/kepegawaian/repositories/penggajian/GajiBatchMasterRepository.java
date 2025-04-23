package id.perumdamts.kepegawaian.repositories.penggajian;

import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface GajiBatchMasterRepository extends JpaRepository<GajiBatchMaster, Long>,
        JpaSpecificationExecutor<GajiBatchMaster> {
    List<GajiBatchMaster> findByGajiBatchRoot_Id(String rootBatchId);
}
