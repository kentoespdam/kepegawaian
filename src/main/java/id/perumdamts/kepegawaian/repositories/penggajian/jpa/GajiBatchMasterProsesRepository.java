package id.perumdamts.kepegawaian.repositories.penggajian.jpa;

import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMasterProses;
import org.springframework.data.jpa.repository.JpaRepository;

import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;

import java.util.Collection;
import java.util.List;

public interface GajiBatchMasterProsesRepository extends JpaRepository<GajiBatchMasterProses, Long> {
    List<GajiBatchMasterProses> findByBatchMasterId(Long batchMasterId);

    void deleteByBatchMasterIdIn(Collection<Long> batchMasterIds);

    boolean existsByBatchMasterIdAndNamaAndJenisGaji(Long batchMasterId, String nama, EJenisGaji jenisGaji);

    List<GajiBatchMasterProses> findByKodeStartingWithAndBatchMasterIdIn(String prefix, Collection<Long> batchMasterIds);
}
