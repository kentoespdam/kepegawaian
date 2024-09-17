package id.perumdamts.kepegawaian.services.master.jenisPelatihan;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.jenisPelatihan.JenisPelatihanPostRequest;
import id.perumdamts.kepegawaian.dto.master.jenisPelatihan.JenisPelatihanPutRequest;
import id.perumdamts.kepegawaian.dto.master.jenisPelatihan.JenisPelatihanRequest;
import id.perumdamts.kepegawaian.dto.master.jenisPelatihan.JenisPelatihanResponse;
import id.perumdamts.kepegawaian.entities.master.JenisPelatihan;
import id.perumdamts.kepegawaian.repositories.master.JenisPelatihanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JenisPelatihanServiceImpl implements JenisPelatihanService {
    private final JenisPelatihanRepository repository;

    @Override
    public List<JenisPelatihanResponse> findAll() {
        return repository.findAll().stream().map(JenisPelatihanResponse::from).toList();
    }

    @Override
    public Page<JenisPelatihanResponse> findPage(JenisPelatihanRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(JenisPelatihanResponse::from);
    }

    @Override
    public JenisPelatihanResponse findById(Long id) {
        return repository.findById(id).map(JenisPelatihanResponse::from).orElse(null);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(JenisPelatihanPostRequest request) {
        Optional<JenisPelatihan> one = repository.findOne(request.getSpecification());
        if (one.isPresent())
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Jenis Pelatihan sudah ada");
        JenisPelatihan entity = JenisPelatihanPostRequest.toEntity(request);
        JenisPelatihan save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Transactional
    @Override
    public SavedStatus<?> saveBatch(List<JenisPelatihanPostRequest> requests) {
        List<JenisPelatihan> entities = JenisPelatihanPostRequest.toEntities(requests);
        repository.saveAll(entities);
        return SavedStatus.build(ESaveStatus.SUCCESS, "Success Saving Batch Data");
    }

    @Override
    public SavedStatus<?> update(Long id, JenisPelatihanPutRequest request) {
        Optional<JenisPelatihan> one = repository.findById(id);
        if (one.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Jenis Pelatihan");
        JenisPelatihan entity = JenisPelatihanPutRequest.toEntity(request, one.get());
        JenisPelatihan save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public Boolean deleteById(Long id) {
        Optional<JenisPelatihan> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }
}
