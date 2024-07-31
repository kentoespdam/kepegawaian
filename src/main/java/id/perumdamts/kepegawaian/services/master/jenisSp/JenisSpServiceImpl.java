package id.perumdamts.kepegawaian.services.master.jenisSp;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpPostRequest;
import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpPutRequest;
import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpRequest;
import id.perumdamts.kepegawaian.dto.master.jenisSp.JenisSpResponse;
import id.perumdamts.kepegawaian.entities.master.JenisSp;
import id.perumdamts.kepegawaian.repositories.master.JenisSpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JenisSpServiceImpl implements JenisSpService {
    private final JenisSpRepository repository;

    @Override
    public List<JenisSpResponse> findAll(JenisSpRequest request) {
        return repository.findAll(request.getSpecification()).stream()
                .map(JenisSpResponse::from).toList();
    }

    @Override
    public Page<JenisSpResponse> findPage(JenisSpRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(JenisSpResponse::from);
    }

    @Override
    public JenisSpResponse findById(Long id) {
        return repository.findById(id).map(JenisSpResponse::from).orElse(null);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(JenisSpPostRequest request) {
        Optional<JenisSp> one = repository.findOne(request.getSpecification());
        if (one.isPresent())
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Jenis Sp sudah ada");
        JenisSp entity = JenisSpPostRequest.toEntity(request);
        JenisSp save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Transactional
    @Override
    public SavedStatus<?> saveBatch(List<JenisSpPostRequest> requests) {
        try {
            requests.stream().map(JenisSpPostRequest::toEntity).forEach(repository::save);
            return SavedStatus.build(ESaveStatus.SUCCESS, null);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    @Override
    public SavedStatus<?> update(Long id, JenisSpPutRequest request) {
        Optional<JenisSp> byId = repository.findById(id);
        if (byId.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Jenis Sp not found");
        JenisSp entity = JenisSpPutRequest.toEntity(byId.get(), request);
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
