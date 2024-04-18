package id.perumdamts.kepegawaian.services.master.jenisKitas;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.jenisKitas.JenisKitasPostRequest;
import id.perumdamts.kepegawaian.dto.master.jenisKitas.JenisKitasPutRequest;
import id.perumdamts.kepegawaian.dto.master.jenisKitas.JenisKitasRequest;
import id.perumdamts.kepegawaian.dto.master.jenisKitas.JenisKitasResponse;
import id.perumdamts.kepegawaian.entities.master.JenisKitas;
import id.perumdamts.kepegawaian.repositories.master.JenisKitasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JenisKitasServiceImpl implements JenisKitasService {
    private final JenisKitasRepository repository;

    @Override
    public List<JenisKitasResponse> findAll() {
        return repository.findAll().stream().map(JenisKitasResponse::from).toList();
    }

    @Override
    public Page<JenisKitasResponse> findPage(JenisKitasRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(JenisKitasResponse::from);
    }

    @Override
    public JenisKitasResponse findById(Long id) {
        return repository.findById(id).map(JenisKitasResponse::from).orElse(null);
    }

    @Override
    public SavedStatus<?> save(JenisKitasPostRequest request) {
        Optional<JenisKitas> one = repository.findOne(request.getSpecification());
        if (one.isPresent())
            return  SavedStatus.build(ESaveStatus.DUPLICATE, "Jenis Kitas sudah ada");
        JenisKitas entity = JenisKitasPostRequest.toEntity(request);
        JenisKitas save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public SavedStatus<?> saveBatch(List<JenisKitasPostRequest> requests) {
        List<JenisKitas> entities = JenisKitasPostRequest.toEntities(requests);
        repository.saveAll(entities);
        return SavedStatus.build(ESaveStatus.SUCCESS, "Success Saving Batch Data");
    }

    @Override
    public SavedStatus<?> update(Long id, JenisKitasPutRequest request) {
        Optional<JenisKitas> one = repository.findById(id);
        if (one.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Jenis Kitas");
        JenisKitas entity = JenisKitasPutRequest.toEntity(request, one.get());
        JenisKitas save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public Boolean deleteById(Long id) {
        Optional<JenisKitas> one = repository.findById(id);
        if (one.isEmpty())
            return false;
        repository.deleteById(id);
        return true;
    }
}
