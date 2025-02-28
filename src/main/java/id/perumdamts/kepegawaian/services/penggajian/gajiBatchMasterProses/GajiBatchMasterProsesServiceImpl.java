package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesResponse;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMasterProses;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchMasterProsesRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GajiBatchMasterProsesServiceImpl implements GajiBatchMasterProsesService {
    private final GajiBatchMasterProsesRepository repository;
    private final GajiBatchMasterRepository gajiBatchMasterRepository;

    @Override
    public Page<GajiBatchMasterProsesResponse> findPage(GajiBatchMasterProsesRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable()).map(GajiBatchMasterProsesResponse::from);
    }

    @Override
    public GajiBatchMasterProsesResponse findById(Long id) {
        return repository.findById(id).map(GajiBatchMasterProsesResponse::from).orElse(null);
    }

    @Override
    public List<GajiBatchMasterProsesResponse> findByMasterId(Long id) {
        return repository.findByGajiBatchMaster_Id(id).stream().map(GajiBatchMasterProsesResponse::from).toList();
    }

    @Override
    public SavedStatus<?> save(GajiBatchMasterProsesPostRequest request) {
        try {
            boolean exists = repository.exists(request.getSpecification());
            if (exists) return SavedStatus.build(ESaveStatus.DUPLICATE, "Gaji Batch Master Proses sudah ada");
            GajiBatchMaster gajiBatchMaster = gajiBatchMasterRepository.findById(request.getMasterBatchId()).orElseThrow(() -> new RuntimeException("Unknown Gaji Batch Master"));
            GajiBatchMasterProses entity = GajiBatchMasterProsesPostRequest.toEntity(request, gajiBatchMaster);
            GajiBatchMasterProses save = repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> rollback(Long batchMasterId) {
        Specification<GajiBatchMasterProses> masterBatchIdSpec = (root, query, cb) -> cb.equal(root.get("gajiBatchMaster").get("id"), batchMasterId);
        Specification<GajiBatchMasterProses> namaSpec = (root, query, cb) -> cb.like(root.get("nama"), "ADD_%");
        Specification<GajiBatchMasterProses> spec = Specification.where(masterBatchIdSpec).and(namaSpec);
        repository.delete(spec);
        return SavedStatus.build(ESaveStatus.SUCCESS, "Rollback success");
    }

    @Override
    public boolean delete(Long id) {
        boolean exists = repository.existsById(id);
        if (!exists)
            return false;
        repository.deleteById(id);
        return true;
    }
}
