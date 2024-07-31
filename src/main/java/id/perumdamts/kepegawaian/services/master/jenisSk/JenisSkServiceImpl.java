package id.perumdamts.kepegawaian.services.master.jenisSk;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.jenisSk.JenisSkPostRequest;
import id.perumdamts.kepegawaian.dto.master.jenisSk.JenisSkPutRequest;
import id.perumdamts.kepegawaian.dto.master.jenisSk.JenisSkRequest;
import id.perumdamts.kepegawaian.dto.master.jenisSk.JenisSkResponse;
import id.perumdamts.kepegawaian.entities.master.JenisSk;
import id.perumdamts.kepegawaian.repositories.master.JenisSkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JenisSkServiceImpl implements JenisSkService {
    private final JenisSkRepository repository;

    @Override
    public List<JenisSkResponse> findAll(JenisSkRequest request) {
        return repository.findAll(request.getSpecification()).stream()
                .map(JenisSkResponse::from).toList();
    }

    @Override
    public Page<JenisSkResponse> findPage(JenisSkRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(JenisSkResponse::from);
    }

    @Override
    public JenisSkResponse findById(Long id) {
        return repository.findById(id).map(JenisSkResponse::from).orElse(null);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(JenisSkPostRequest request) {
        boolean exists = repository.exists(request.getSpecification());
        if (exists)
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Jenis SK sudah ada");
        JenisSk entity = JenisSkPostRequest.toEntity(request);
        JenisSk save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Transactional
    @Override
    public SavedStatus<?> saveBatch(List<JenisSkPostRequest> requests) {
        requests.stream().map(JenisSkPostRequest::toEntity).forEach(repository::save);
        return SavedStatus.build(ESaveStatus.SUCCESS, null);
    }

    @Transactional
    @Override
    public SavedStatus<?> update(Long id, JenisSkPutRequest request) {
        Optional<JenisSk> byId = repository.findById(id);
        if (byId.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Jenis SK not found");
        JenisSk entity = JenisSkPutRequest.toEntity(byId.get(), request);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, null);
    }

    @Transactional
    @Override
    public boolean deleteById(Long id) {
        boolean exists = repository.existsById(id);
        if (!exists)
            return false;
        repository.deleteById(id);
        return true;
    }
}
