package id.perumdamts.kepegawaian.services.master.organisasi;

import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiPostRequest;
import id.perumdamts.kepegawaian.entities.master.Organisasi;

import java.util.List;

public interface OrganisasiService {
    List<Organisasi> findAll();

    Organisasi findById(Long id);
    Organisasi save(OrganisasiPostRequest request);
    Organisasi update(Long id, Organisasi organisasi);
    Boolean deleteById(Long id);
}
