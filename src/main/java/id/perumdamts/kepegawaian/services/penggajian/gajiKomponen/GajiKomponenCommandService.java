package id.perumdamts.kepegawaian.services.penggajian.gajiKomponen;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen.GajiKomponenPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen.GajiKomponenPutRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiKomponen;
import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;
import id.perumdamts.kepegawaian.mapper.penggajian.gajiKomponen.GajiKomponenMapper;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiKomponenRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiProfilRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiKomponenCommandService {
    private final GajiKomponenRepository repository;
    private final GajiProfilRepository gajiProfilRepository;

    @Transactional
    public SavedStatus<?> create(GajiKomponenPostRequest request) {
        try {
            boolean exists = repository.exists(request.getSpecification());
            if (exists) return SavedStatus.build(ESaveStatus.DUPLICATE, "Gaji Komponen sudah ada");
            GajiProfil gajiProfil = gajiProfilRepository.findById(request.getProfilGajiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Profil Gaji"));
            GajiKomponen entity = GajiKomponenMapper.toEntity(request, gajiProfil);
            repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Komponen Gaji Saved");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    public SavedStatus<?> update(Long id, GajiKomponenPutRequest request) {
        try {
            GajiKomponen gajiKomponen = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Unknown Gaji Komponen"));
            GajiProfil gajiProfil = gajiProfilRepository.findById(request.getProfilGajiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Profil Gaji"));
            GajiKomponenMapper.updateEntity(gajiKomponen, request, gajiProfil);
            repository.save(gajiKomponen);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Komponen Gaji Updated");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    public Boolean delete(Long id) {
        Optional<GajiKomponen> byId = repository.findById(id);
        if (byId.isEmpty()) return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }

    @Transactional
    public void generateDefaultValue(GajiProfil profilGaji) {
        repository.save(defaultGP(profilGaji));
        repository.save(defaultJmlAnak(profilGaji));
        repository.save(defaultJmlJiwa(profilGaji));
    }

    private GajiKomponen defaultGP(GajiProfil profilGaji) {
        GajiKomponen entity = new GajiKomponen();
        entity.setProfilGaji(profilGaji);
        entity.setUrut(1);
        entity.setKode("GP");
        entity.setNama("Gaji Pokok");
        entity.setJenisGaji(EJenisGaji.PEMASUKAN);
        entity.setNilai(0.0);
        entity.setIsReference(true);
        entity.setFormula("#SYSTEM");
        return entity;
    }

    private GajiKomponen defaultJmlAnak(GajiProfil profilGaji) {
        GajiKomponen entity = new GajiKomponen();
        entity.setProfilGaji(profilGaji);
        entity.setUrut(2);
        entity.setKode("JML_ANAK");
        entity.setNama("Jumlah Anak");
        entity.setJenisGaji(EJenisGaji.NONE);
        entity.setNilai(0.0);
        entity.setIsReference(true);
        entity.setFormula("#SYSTEM");
        return entity;
    }

    private GajiKomponen defaultJmlJiwa(GajiProfil profilGaji) {
        GajiKomponen entity = new GajiKomponen();
        entity.setProfilGaji(profilGaji);
        entity.setUrut(3);
        entity.setKode("JML_JIWA");
        entity.setNama("Jumlah Jiwa");
        entity.setJenisGaji(EJenisGaji.NONE);
        entity.setNilai(0.0);
        entity.setIsReference(true);
        entity.setFormula("#SYSTEM");
        return entity;
    }
}
