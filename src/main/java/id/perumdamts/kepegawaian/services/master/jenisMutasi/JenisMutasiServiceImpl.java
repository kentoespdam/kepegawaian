package id.perumdamts.kepegawaian.services.master.jenisMutasi;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.jenisMutasi.JenisMutasiPostRequest;
import id.perumdamts.kepegawaian.dto.master.jenisMutasi.JenisMutasiPutRequest;
import id.perumdamts.kepegawaian.dto.master.jenisMutasi.JenisMutasiRequest;
import id.perumdamts.kepegawaian.dto.master.jenisMutasi.JenisMutasiResponse;
import id.perumdamts.kepegawaian.entities.master.JenisMutasi;
import id.perumdamts.kepegawaian.repositories.master.JenisMutasiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JenisMutasiServiceImpl implements JenisMutasiService {
    private final JenisMutasiRepository repository;

    @Override
    public List<JenisMutasiResponse> findAll(JenisMutasiRequest request) {
        return repository.findAll(request.getSpecification())
                .stream().map(JenisMutasiResponse::from).toList();
    }

    @Override
    public Page<JenisMutasiResponse> findPage(JenisMutasiRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(JenisMutasiResponse::from);
    }

    @Override
    public JenisMutasiResponse findById(Long id) {
        return repository.findById(id).map(JenisMutasiResponse::from).orElse(null);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(JenisMutasiPostRequest request) {
        boolean exists = repository.exists(request.getSpecification());
        if (exists)
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Jenis Mutasi sudah ada");
        JenisMutasi entity = JenisMutasiPostRequest.toEntity(request);
        JenisMutasi save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Transactional
    @Override
    public SavedStatus<?> saveBatch(List<JenisMutasiPostRequest> requests) {
        requests.stream().map(JenisMutasiPostRequest::toEntity).forEach(repository::save);
        return SavedStatus.build(ESaveStatus.SUCCESS, null);
    }

    @Transactional
    @Override
    public SavedStatus<?> update(Long id, JenisMutasiPutRequest request) {
        Optional<JenisMutasi> byId = repository.findById(id);
        if (byId.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Jenis Mutasi not found");
        JenisMutasi entity = JenisMutasiPutRequest.toEntity(byId.get(), request);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, null);
    }

    @Override
    public boolean deleteById(Long id) {
        boolean exists = repository.existsById(id);
        if (!exists)
            return false;
        repository.deleteById(id);
        return true;
    }
}
