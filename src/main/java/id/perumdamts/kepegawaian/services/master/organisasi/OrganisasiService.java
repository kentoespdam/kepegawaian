package id.perumdamts.kepegawaian.services.master.organisasi;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiPostRequest;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiPutRequest;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiRequest;
import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrganisasiService {
    List<OrganisasiResponse> findAll(OrganisasiRequest request);

    Page<OrganisasiResponse> findPage(OrganisasiRequest request);

    OrganisasiResponse findById(Long id);

    OrganisasiResponse findByParentId(Long id);

    SavedStatus<?> save(OrganisasiPostRequest request);

    SavedStatus<?> saveBatch(List<OrganisasiPostRequest> requests);

    SavedStatus<?> update(Long id, OrganisasiPutRequest request);

    Boolean deleteById(Long id);
}
