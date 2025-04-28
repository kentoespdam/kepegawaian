package id.perumdamts.kepegawaian.services.master.jenisSp;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.jenisSp.*;
import id.perumdamts.kepegawaian.entities.master.JenisSp;
import id.perumdamts.kepegawaian.repositories.master.JenisSpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JenisSpServiceImpl implements JenisSpService {
    private final JenisSpRepository repository;

    @Override
    public Page<JenisSpResponse> findPage(JenisSpRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(JenisSpResponse::from);
    }

    @Override
    public List<JenisSpMiniResponse> findList() {
        return repository.findAll().stream().map(JenisSpMiniResponse::from).toList();
    }

    @Override
    public JenisSpResponse findById(Long id) {
        return repository.findById(id).map(JenisSpResponse::from).orElse(null);
    }

    @Override
    public SavedStatus<?> save(JenisSpPostRequest request) {
        boolean exists = repository.exists(request.getSpecification());
        if (exists) return SavedStatus.build(ESaveStatus.DUPLICATE, "Jenis SP sudah ada");
        JenisSp entity = JenisSpPostRequest.toEntity(request);
        JenisSp save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public SavedStatus<?> update(Long id, JenisSpPutRequest request) {
        Optional<JenisSp> one = repository.findById(id);
        if (one.isEmpty()) return SavedStatus.build(ESaveStatus.FAILED, "Unknown Jenis SP");
        JenisSp entity = JenisSpPutRequest.toEntity(one.get(), request);
        JenisSp save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public boolean delete(Long id) {
        Optional<JenisSp> byId = repository.findById(id);
        if (byId.isEmpty()) return false;
        JenisSp jenisSp = byId.get();
        jenisSp.setIsDeleted(true);
        repository.save(jenisSp);
        return true;
    }
}
