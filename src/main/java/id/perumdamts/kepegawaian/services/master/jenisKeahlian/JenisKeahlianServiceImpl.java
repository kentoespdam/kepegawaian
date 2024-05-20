package id.perumdamts.kepegawaian.services.master.jenisKeahlian;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.jenisKeahlian.JenisKeahlianPostRequest;
import id.perumdamts.kepegawaian.dto.master.jenisKeahlian.JenisKeahlianPutRequest;
import id.perumdamts.kepegawaian.dto.master.jenisKeahlian.JenisKeahlianRequest;
import id.perumdamts.kepegawaian.dto.master.jenisKeahlian.JenisKeahlianResponse;
import id.perumdamts.kepegawaian.entities.master.JenisKeahlian;
import id.perumdamts.kepegawaian.repositories.master.JenisKeahlianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JenisKeahlianServiceImpl implements JenisKeahlianService {
    private final JenisKeahlianRepository repository;

    @Override
    public List<JenisKeahlianResponse> findAll() {
        return repository.findAll().stream().map(JenisKeahlianResponse::from).toList();
    }

    @Override
    public Page<JenisKeahlianResponse> findPage(JenisKeahlianRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(JenisKeahlianResponse::from);
    }

    @Override
    public JenisKeahlianResponse findById(Long id) {
        return repository.findById(id).map(JenisKeahlianResponse::from).orElse(null);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(JenisKeahlianPostRequest request) {
        Optional<JenisKeahlian> one = repository.findOne(request.getSpecification());
        if (one.isPresent())
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Jenis Keahlian sudah ada");
        JenisKeahlian entity = JenisKeahlianPostRequest.toEntity(request);
        JenisKeahlian save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Transactional
    @Override
    public SavedStatus<?> saveBatch(List<JenisKeahlianPostRequest> requests) {
        List<JenisKeahlian> entities = JenisKeahlianPostRequest.toEntities(requests);
        repository.saveAll(entities);
        return SavedStatus.build(ESaveStatus.SUCCESS, "Success Saving Batch Data");
    }

    @Override
    public SavedStatus<?> update(Long id, JenisKeahlianPutRequest request) {
        Optional<JenisKeahlian> one = repository.findById(id);
        if (one.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Jenis Keahlian");
        JenisKeahlian entity = JenisKeahlianPutRequest.toEntity(request, one.get());
        JenisKeahlian save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public Boolean deleteById(Long id) {
        Optional<JenisKeahlian> one = repository.findById(id);
        if (one.isEmpty())
            return false;
        repository.deleteById(id);
        return true;
    }
}
