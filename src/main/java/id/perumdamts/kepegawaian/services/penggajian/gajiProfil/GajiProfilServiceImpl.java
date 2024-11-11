package id.perumdamts.kepegawaian.services.penggajian.gajiProfil;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilPutRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilResponse;
import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiProfilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiProfilServiceImpl implements GajiProfilService {
    private final GajiProfilRepository repository;

    @Override
    public List<GajiProfilResponse> findAll() {
        return repository.findAll().stream().map(GajiProfilResponse::from).toList();
    }

    @Override
    public GajiProfilResponse findById(Long id) {
        return repository.findById(id).map(GajiProfilResponse::from).orElse(null);
    }

    @Override
    public SavedStatus<?> create(GajiProfilPostRequest request) {
        boolean exists = repository.exists(request.getSpecification());
        if (exists) return SavedStatus.build(ESaveStatus.DUPLICATE, "Gaji Profil sudah ada");
        GajiProfil entity = GajiProfilPostRequest.toEntity(request);
        GajiProfil save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public SavedStatus<?> update(Long id, GajiProfilPutRequest request) {
        Optional<GajiProfil> byId = repository.findById(id);
        if (byId.isEmpty()) return SavedStatus.build(ESaveStatus.FAILED, "Gaji Profil not found");
        GajiProfil entity = GajiProfilPutRequest.toEntity(byId.get(), request);
        GajiProfil save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Override
    public boolean delete(Long id) {
        Optional<GajiProfil> byId = repository.findById(id);
        if (byId.isEmpty()) return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }
}
