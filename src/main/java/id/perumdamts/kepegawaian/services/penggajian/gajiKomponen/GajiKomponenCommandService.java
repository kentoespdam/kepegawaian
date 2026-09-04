package id.perumdamts.kepegawaian.services.penggajian.gajiKomponen;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen.GajiKomponenPostRequest;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen.GajiKomponenPutRequest;
import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiKomponen;
import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.penggajian.gajiKomponen.GajiKomponenMapper;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiKomponenRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiProfilRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiKomponenCommandService {
    private final GajiKomponenRepository repository;
    private final GajiProfilRepository gajiProfilRepository;

    @Transactional
    @CacheEvict(value = "gaji-referensi", key = "'komponen'")
    public SavedStatus<Long> create(GajiKomponenPostRequest request) {
        boolean exists = repository.exists(request.getSpecification());
        if (exists) throw new ConflictException("Gaji Komponen sudah ada");
        GajiProfil gajiProfil = gajiProfilRepository.findById(request.getProfilGajiId())
                .orElseThrow(() -> new NotFoundException("Profil Gaji not found"));
        GajiKomponen entity = GajiKomponenMapper.toEntity(request, gajiProfil);
        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, entity.getId());
    }

    @Transactional
    @CacheEvict(value = "gaji-referensi", key = "'komponen'")
    public SavedStatus<Long> update(Long id, GajiKomponenPutRequest request) {
        GajiKomponen gajiKomponen = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Gaji Komponen not found"));
        GajiProfil gajiProfil = gajiProfilRepository.findById(request.getProfilGajiId())
                .orElseThrow(() -> new NotFoundException("Profil Gaji not found"));
        GajiKomponenMapper.updateEntity(gajiKomponen, request, gajiProfil);
        repository.save(gajiKomponen);
        return SavedStatus.build(ESaveStatus.SUCCESS, gajiKomponen.getId());
    }

    @Transactional
    @CacheEvict(value = "gaji-referensi", key = "'komponen'")
    public Boolean delete(Long id) {
        Optional<GajiKomponen> byId = repository.findById(id);
        if (byId.isEmpty()) return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }

    @Transactional
    @CacheEvict(value = "gaji-referensi", key = "'komponen'")
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
