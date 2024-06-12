package id.perumdamts.kepegawaian.services.master.organisasi;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiPostRequest;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiPutRequest;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiRequest;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiResponse;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.repositories.master.OrganisasiRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganisasiServiceImpl implements OrganisasiService {
    private final OrganisasiRepository repository;

    @Override
    public List<OrganisasiResponse> findAll() {
        return repository.findAll().stream().map(OrganisasiResponse::from).toList();
    }

    @Override
    public Page<OrganisasiResponse> findPage(OrganisasiRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(OrganisasiResponse::from);
    }

    @Override
    public OrganisasiResponse findById(Long id) {
        return repository.findById(id)
                .map(OrganisasiResponse::from)
                .orElse(null);
    }

    @Override
    public OrganisasiResponse findByParentId(Long id) {
        return repository.findById(id)
                .map(OrganisasiResponse::from)
                .orElse(null);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(OrganisasiPostRequest request) {
        Organisasi parent = Objects.isNull(request.getParentId()) ? null :
                repository.findById(request.getParentId()).orElse(null);
        Optional<Organisasi> cari = repository.findOne(request.getSpecification());
        if (cari.isPresent())
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Organisasi sudah ada");
        Organisasi entity = OrganisasiPostRequest.toEntity(request, parent);
        Organisasi save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Transactional
    @Override
    public SavedStatus<?> saveBatch(List<OrganisasiPostRequest> requests) {
        List<Organisasi> entities = requests.stream().map(request -> {
            Organisasi parent = Objects.isNull(request.getParentId()) ? null :
                    repository.findById(request.getParentId()).orElse(null);
            return OrganisasiPostRequest.toEntity(request, parent);
        }).toList();
        repository.saveAll(entities);
        return SavedStatus.build(ESaveStatus.SUCCESS, "Success Saving Batch Data");
    }

    @Transactional
    @Override
    public SavedStatus<?> update(Long id, OrganisasiPutRequest request) {
        Organisasi parent = Objects.isNull(request.getParentId()) ? null :
                repository.findById(request.getParentId()).orElse(null);
        Optional<Organisasi> byId = repository.findById(id);
        if (byId.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Organisasi");
        Organisasi entity = OrganisasiPutRequest.toEntity(byId.get(), request, parent);
        Organisasi save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Transactional
    @Override
    public Boolean deleteById(Long id) {
        Optional<Organisasi> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        repository.deleteById(id);
        return true;
    }
}
