package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiBatchMasterProses.GajiBatchMasterProsesResponse;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMasterProses;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchMasterProsesRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GajiBatchMasterProsesServiceImpl implements GajiBatchMasterProsesService {
    @Value("${penggajian.endpoint}")
    private String ENDPOINT;
    private final GajiBatchMasterProsesRepository repository;
    private final GajiBatchMasterRepository gajiBatchMasterRepository;
    private final WebClient webClient;

    @Override
    public Page<GajiBatchMasterProsesResponse> findPage(GajiBatchMasterProsesRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(GajiBatchMasterProsesResponse::from);
    }

    @Override
    public GajiBatchMasterProsesResponse findById(Long id) {
        return repository.findById(id).map(GajiBatchMasterProsesResponse::from).orElse(null);
    }

    @Override
    public List<GajiBatchMasterProsesResponse> findByMasterId(Long id) {
        Specification<GajiBatchMasterProses> where = Specification.where((root, query, cb) ->
                cb.equal(root.get("gajiBatchMaster").get("id"), id));
        return repository.findAll(where).stream().map(GajiBatchMasterProsesResponse::from).toList();
    }

    @Override
    public SavedStatus<?> save(GajiBatchMasterProsesPostRequest request) {
        boolean exists = repository.exists(request.getSpecification());
        if (exists) return SavedStatus.build(ESaveStatus.DUPLICATE, "Komponen Gaji sudah ada");
        gajiBatchMasterRepository.findById(request.getMasterBatchId())
                .orElseThrow(() -> new RuntimeException("Unknown Gaji Batch Master"));
        GajiBatchMasterProses entity = GajiBatchMasterProsesPostRequest.toEntity(request);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, entity);
    }

    @Override
    public boolean rollback(String rootBatchId) {
        webClient.delete()
                .uri(ENDPOINT+"/rollback/"+rootBatchId+"/additional_gaji")
                .retrieve()
                .bodyToMono(String.class);

        return true;
    }

    @Override
    public boolean delete(Long id) {
        Optional<GajiBatchMasterProses> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        webClient.delete()
                .uri(ENDPOINT+"/rollback/"+byId.get().getId()+"/master_batch")
                .retrieve()
                .bodyToMono(String.class);
        repository.deleteById(id);
        return true;
    }
}
