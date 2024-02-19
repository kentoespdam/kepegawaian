package id.perumdamts.kepegawaian.services.master.organisasi;

import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiPostRequest;
import id.perumdamts.kepegawaian.entities.master.Organisasi;
import id.perumdamts.kepegawaian.repositories.master.OrganisasiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganisasiServiceImpl implements OrganisasiService {
    private final OrganisasiRepository repository;

    @Override
    public List<Organisasi> findAll() {
        return repository.findAll();
    }

    @Override
    public Organisasi findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Organisasi save(OrganisasiPostRequest request) {
        Organisasi entity = request.toEntity(request);
        return repository.save(entity);
    }

    @Override
    public Organisasi update(Long id, Organisasi organisasi) {
        return repository.save(organisasi);
    }

    @Override
    public Boolean deleteById(Long id) {
        repository.deleteById(id);
        return true;
    }
}
