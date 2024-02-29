package id.perumdamts.kepegawaian.services.master.statusPegawai;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.statusPegawai.StatusPegawaiPostRequest;
import id.perumdamts.kepegawaian.dto.master.statusPegawai.StatusPegawaiPutRequest;
import id.perumdamts.kepegawaian.dto.master.statusPegawai.StatusPegawaiRequest;
import id.perumdamts.kepegawaian.dto.master.statusPegawai.StatusPegawaiResponse;
import id.perumdamts.kepegawaian.entities.master.StatusPegawai;
import id.perumdamts.kepegawaian.repositories.master.StatusPegawaiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatusPegawaiServiceImpl implements StatusPegawaiService {
    public final StatusPegawaiRepository repository;

    @Override
    public List<StatusPegawaiResponse> findAll() {
        return repository.findAll().stream().map(StatusPegawaiResponse::from).toList();
    }

    @Override
    public Page<StatusPegawaiResponse> findPage(StatusPegawaiRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(StatusPegawaiResponse::from);
    }

    @Override
    public StatusPegawaiResponse findById(Long id) {
        return repository.findById(id)
                .map(StatusPegawaiResponse::from)
                .orElse(null);
    }

    @Override
    public SavedStatus<?> save(StatusPegawaiPostRequest request) {
        Optional<StatusPegawai> cari = repository.findByNama(request.getNama());
        if (cari.isPresent())
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Organisasi sudah ada");
        StatusPegawai entity = StatusPegawaiPostRequest.toEntity(request);
        StatusPegawai save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public SavedStatus<?> saveBatch(List<StatusPegawaiPostRequest> requests) {
        List<StatusPegawai> entities = StatusPegawaiPostRequest.toEntities(requests);
        repository.saveAll(entities);
        return SavedStatus.build(ESaveStatus.SUCCESS, "Success Saving Batch Data");
    }

    @Override
    public SavedStatus<?> update(Long id, StatusPegawaiPutRequest request) {
        Optional<StatusPegawai> byId = repository.findById(id);
        if (byId.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Status Pegawai");
        StatusPegawai entity = StatusPegawaiPutRequest.toEntity(request, id);
        StatusPegawai save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public Boolean deleteById(Long id) {
        Optional<StatusPegawai> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        repository.deleteById(id);
        return true;
    }
}
