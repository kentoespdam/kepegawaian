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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiBatchMasterProsesServiceImpl implements GajiBatchMasterProsesService {
    @Value("${penggajian.endpoint}")
    private String ENDPOINT;
    private final GajiBatchMasterProsesRepository repository;
    private final GajiBatchMasterRepository gajiBatchMasterRepository;
    private final WebClient webClient;

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
            repository.save(entity);
            recalculate(request.getMasterBatchId());
            return SavedStatus.build(ESaveStatus.SUCCESS, "OK");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public boolean rollback(String rootBatchId) {
        Specification<GajiBatchMasterProses> rootBatchIdSpec = (root, query, cb) -> cb.equal(root.get("gajiBatchMaster").get("gajiBatchRoot").get("id"), rootBatchId);
        Specification<GajiBatchMasterProses> kodeSpec = (root, query, cb) -> cb.like(root.get("kode"), "ADD_%");
        Specification<GajiBatchMasterProses> spec = Specification.where(rootBatchIdSpec).and(kodeSpec);
        List<GajiBatchMasterProses> all = repository.findAll(spec);
        repository.deleteAll(all);
        all.forEach(gajiBatchMasterProses -> recalculate(gajiBatchMasterProses.getGajiBatchMaster().getId()));
        return true;
    }

    @Override
    public boolean delete(Long id) {
        Optional<GajiBatchMasterProses> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        GajiBatchMaster gajiBatchMaster = byId.get().getGajiBatchMaster();
        recalculate(gajiBatchMaster.getId());
        repository.deleteById(id);
        return true;
    }

    private void recalculate(Long masterBatchId) {
        webClient.get()
                .uri(ENDPOINT + "/recalculate/" + masterBatchId)
                .retrieve();
    }
}
