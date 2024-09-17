package id.perumdamts.kepegawaian.services.master.jenjangPendidikan;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanPostRequest;
import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanPutRequest;
import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanRequest;
import id.perumdamts.kepegawaian.dto.master.jenjangPendidikan.JenjangPendidikanResponse;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.repositories.master.JenjangPendidikanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JenjangPendidikanServiceImpl implements JenjangPendidikanService {
    private final JenjangPendidikanRepository repository;

    @Override
    public List<JenjangPendidikanResponse> findAll() {
        return repository.findAll().stream().map(JenjangPendidikanResponse::from).toList();
    }

    @Override
    public Page<JenjangPendidikanResponse> findPage(JenjangPendidikanRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(JenjangPendidikanResponse::from);
    }

    @Override
    public JenjangPendidikanResponse findById(Long id) {
        return repository.findById(id).map(JenjangPendidikanResponse::from).orElse(null);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(JenjangPendidikanPostRequest request) {
        Optional<JenjangPendidikan> one = repository.findOne(request.getSpecification());
        if (one.isPresent())
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Jenjang Pendidikan sudah ada");
        JenjangPendidikan entity = JenjangPendidikanPostRequest.toEntity(request);
        JenjangPendidikan save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Transactional
    @Override
    public SavedStatus<?> saveBatch(List<JenjangPendidikanPostRequest> requests) {
        List<JenjangPendidikan> entities = JenjangPendidikanPostRequest.toEntities(requests);
        repository.saveAll(entities);
        return SavedStatus.build(ESaveStatus.SUCCESS, "Success Saving Batch Data");
    }

    @Override
    public SavedStatus<?> update(Long id, JenjangPendidikanPutRequest request) {
        Optional<JenjangPendidikan> one = repository.findById(id);
        if (one.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Jenjang Pendidikan");
        JenjangPendidikan entity = JenjangPendidikanPutRequest.toEntity(request, one.get());
        JenjangPendidikan save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public Boolean deleteById(Long id) {
        Optional<JenjangPendidikan> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }
}
