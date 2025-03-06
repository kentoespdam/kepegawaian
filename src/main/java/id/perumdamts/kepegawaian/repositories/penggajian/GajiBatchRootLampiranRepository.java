package id.perumdamts.kepegawaian.repositories.penggajian;

import id.perumdamts.kepegawaian.entities.commons.EJenisPotonganGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRootLampiran;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface GajiBatchRootLampiranRepository extends JpaRepository<GajiBatchRootLampiran, Long>, JpaSpecificationExecutor<GajiBatchRootLampiran> {
    List<GajiBatchRootLampiran> findByGajiBatchRoot_IdAndJenisLampiranGaji(String rootBatchId, EJenisPotonganGaji potonganTkk);
}
