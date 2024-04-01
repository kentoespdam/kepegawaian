package id.perumdamts.kepegawaian.services.master.jabatan;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanPostRequest;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanRequest;
import id.perumdamts.kepegawaian.dto.master.jabatan.JabatanResponse;
import id.perumdamts.kepegawaian.entities.master.*;
import id.perumdamts.kepegawaian.repositories.master.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JabatanServiceImpl implements JabatanService {
    private final JabatanRepository repository;
    private final OrganisasiRepository organisasiRepository;
    private final LevelRepository levelRepository;

    @Override
    public List<JabatanResponse> findAll() {
        return repository.findAll().stream().map(JabatanResponse::from).toList();
    }

    @Override
    public Page<JabatanResponse> findPage(JabatanRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable()).map(JabatanResponse::from);
    }

    @Override
    public JabatanResponse findById(Long id) {
        return repository.findById(id).map(JabatanResponse::from).orElse(null);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(JabatanPostRequest request) {
        Optional<Organisasi> organisasi = organisasiRepository.findById(request.getOrganisasiId());
        if (organisasi.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Organisasi");
        Optional<Level> level = levelRepository.findById(request.getLevelId());
        if (level.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Level");
        Jabatan parent = Objects.nonNull(request.getParentId()) ?
                repository.findById(request.getParentId()).orElse(null) :
                null;
        List<Jabatan> all = repository.findAll(request.getSpecification());
        if (!all.isEmpty())
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Jabatan sudah ada");
        Jabatan entity = JabatanPostRequest.toEntity(request, parent);
        Jabatan save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Transactional
    @Override
    public SavedStatus<?> saveBatch(List<JabatanPostRequest> requests) {
        List<Jabatan> entities = JabatanPostRequest.toEntities(requests);
        repository.saveAll(entities);
        return SavedStatus.build(ESaveStatus.SUCCESS, "Success Saving Batch Data");
    }

    @Transactional
    @Override
    public SavedStatus<?> update(Long id, JabatanPostRequest request) {
        Optional<Organisasi> organisasi = organisasiRepository.findById(request.getOrganisasiId());
        if (organisasi.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Organisasi");
        Optional<Level> level = levelRepository.findById(request.getLevelId());
        if (level.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Level");
        Optional<Jabatan> byId = repository.findById(id);
        if (byId.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Jabatan");
        Jabatan entity = JabatanPostRequest.toEntity(request, id);
        Jabatan save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Transactional
    @Override
    public Boolean deleteById(Long id) {
        Optional<Jabatan> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        repository.deleteById(id);
        return true;
    }
}
