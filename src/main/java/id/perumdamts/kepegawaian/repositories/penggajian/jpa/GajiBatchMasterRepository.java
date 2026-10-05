package id.perumdamts.kepegawaian.repositories.penggajian.jpa;

import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GajiBatchMasterRepository extends JpaRepository<GajiBatchMaster, Long> {
    List<GajiBatchMaster> findByGajiBatchRoot_Id(String rootBatchId);
}
