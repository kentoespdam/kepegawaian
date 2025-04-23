package id.perumdamts.kepegawaian.repositories.penggajian;

import id.perumdamts.kepegawaian.entities.commons.EJenisPotonganGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRootLampiran;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GajiBatchRootLampiranRepository extends JpaRepository<GajiBatchRootLampiran, Long> {
    List<GajiBatchRootLampiran> findByGajiBatchRoot_IdAndJenisLampiranGaji(String rootBatchId, EJenisPotonganGaji eJenisPotonganGaji);
}
