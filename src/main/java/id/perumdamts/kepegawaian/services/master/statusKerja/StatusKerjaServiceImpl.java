package id.perumdamts.kepegawaian.services.master.statusKerja;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.statusKerja.StatusKerjaPostRequest;
import id.perumdamts.kepegawaian.dto.master.statusKerja.StatusKerjaPutRequest;
import id.perumdamts.kepegawaian.dto.master.statusKerja.StatusKerjaRequest;
import id.perumdamts.kepegawaian.dto.master.statusKerja.StatusKerjaResponse;
import id.perumdamts.kepegawaian.entities.master.StatusKerja;
import id.perumdamts.kepegawaian.repositories.master.StatusKerjaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatusKerjaServiceImpl implements StatusKerjaService {
    private final StatusKerjaRepository repository;

    @Override
    public List<StatusKerjaResponse> findAll() {
        return repository.findAll().stream().map(StatusKerjaResponse::from).toList();
    }

    @Override
    public Page<StatusKerjaResponse> findPage(StatusKerjaRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(StatusKerjaResponse::from);
    }

    @Override
    public StatusKerjaResponse findById(Long id) {
        return repository.findById(id)
                .map(StatusKerjaResponse::from)
                .orElse(null);
    }

    @Override
    public SavedStatus<?> save(StatusKerjaPostRequest request) {
        Optional<StatusKerja> cari = repository.findOne(request.getSpecification());
        if (cari.isPresent())
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Status Kerja sudah ada");
        StatusKerja entity = StatusKerjaPostRequest.toEntity(request);
        StatusKerja save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public SavedStatus<?> saveBatch(List<StatusKerjaPostRequest> requests) {
        List<StatusKerja> entities = StatusKerjaPostRequest.toEntities(requests);
        repository.saveAll(entities);
        return SavedStatus.build(ESaveStatus.SUCCESS, "Success Saving Batch Data");
    }

    @Override
    public SavedStatus<?> update(Long id, StatusKerjaPutRequest request) {
        Optional<StatusKerja> byId = repository.findById(id);
        if (byId.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Status Kerja");

        StatusKerja entity = StatusKerjaPutRequest.toEntity(byId.get(), request);
        StatusKerja save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public Boolean deleteById(Long id) {
        boolean exists = repository.existsById(id);
        if (!exists)
            return false;
        repository.deleteById(id);
        return true;
    }
}
