package id.perumdamts.kepegawaian.services.penggajian.gajiProfil;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiProfil.GajiProfilPutRequest;
import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;
import id.perumdamts.kepegawaian.mapper.penggajian.gajiProfil.GajiProfilMapper;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiProfilRepository;
import id.perumdamts.kepegawaian.services.penggajian.gajiKomponen.GajiKomponenCommandService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiProfilCommandService {
    private final GajiProfilRepository repository;
    private final GajiKomponenCommandService gajiKomponenService;

    @Transactional
    public SavedStatus<?> create(GajiProfilPostRequest request) {
        boolean exists = repository.exists(request.getSpecification());
        if (exists) return SavedStatus.build(ESaveStatus.DUPLICATE, "Gaji Profil sudah ada");
        GajiProfil entity = GajiProfilMapper.toEntity(request);
        GajiProfil save = repository.save(entity);
        gajiKomponenService.generateDefaultValue(save);
        return SavedStatus.build(ESaveStatus.SUCCESS, "Gaji Profil Saved");
    }

    @Transactional
    public SavedStatus<?> update(Long id, GajiProfilPutRequest request) {
        Optional<GajiProfil> byId = repository.findById(id);
        if (byId.isEmpty()) return SavedStatus.build(ESaveStatus.FAILED, "Gaji Profil not found");
        GajiProfil entity = byId.get();
        GajiProfilMapper.updateEntity(entity, request);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, "Gaji Profil Updated");
    }

    @Transactional
    public boolean delete(Long id) {
        Optional<GajiProfil> byId = repository.findById(id);
        if (byId.isEmpty()) return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }
}
