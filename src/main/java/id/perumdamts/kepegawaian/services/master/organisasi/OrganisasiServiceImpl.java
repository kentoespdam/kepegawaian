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

import java.util.List;
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
        return repository.findAll(
                        request.getSpecification(), request.getPageable()
                )
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
        return repository.findByOrganisasi_Id(id)
                .map(OrganisasiResponse::from)
                .orElse(null);
    }

    @Override
    public SavedStatus<?> save(OrganisasiPostRequest request) {
        Optional<Organisasi> cari = repository.findByOrganisasi_IdAndLevelOrgAndNama(request.getParentId(), request.getLevelOrganisasi(), request.getNama());
        if (cari.isPresent())
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Organisasi sudah ada");
        Organisasi entity = OrganisasiPostRequest.toEntity(request);
        Organisasi save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public SavedStatus<?> saveBatch(List<OrganisasiPostRequest> requests) {
        List<Organisasi> entities = OrganisasiPostRequest.toEntities(requests);
        repository.saveAll(entities);
        return SavedStatus.build(ESaveStatus.SUCCESS, "Success Saving Batch Data");
    }

    @Override
    public SavedStatus<?> update(Long id, OrganisasiPutRequest request) {
        Optional<Organisasi> byId = repository.findById(id);
        if (byId.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Organisasi");
        Organisasi entity = OrganisasiPutRequest.toEntity(request, id);
        Organisasi save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public Boolean deleteById(Long id) {
        Optional<Organisasi> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        repository.deleteById(id);
        return true;
    }
}
