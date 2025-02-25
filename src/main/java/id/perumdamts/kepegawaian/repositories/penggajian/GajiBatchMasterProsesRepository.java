package id.perumdamts.kepegawaian.repositories.penggajian;

import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMasterProses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface GajiBatchMasterProsesRepository extends JpaRepository<GajiBatchMasterProses, Long>, JpaSpecificationExecutor<GajiBatchMasterProses> {
    List<GajiBatchMasterProses> findByGajiBatchMaster_Id(Long id);
}
