package id.perumdamts.kepegawaian.services.penggajian.gajiKomponen;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.penggajian.gajiKomponen.*;
import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiKomponen;
import id.perumdamts.kepegawaian.entities.penggajian.GajiProfil;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiKomponenRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiProfilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GajiKomponenServiceImpl implements GajiKomponenService {
    private final GajiKomponenRepository repository;
    private final GajiProfilRepository gajiProfilRepository;

    @Override
    public List<GajiKomponenMiniProjection> findAllKode(Long profilId) {
        return repository.findAllKode(profilId);
    }

    @Override
    public Page<GajiKomponenResponse> findByProfil(Long id, GajiKomponenRequest request) {
        request.setProfilId(id);
        return repository.findAll(request.getSpecification(), request.getPageable()).map(GajiKomponenResponse::from);
    }

    @Override
    public Integer findLastUrut(Long profilId) {
        return repository.findLastUrut(profilId);
    }

    @Override
    public GajiKomponenResponse findDetail(Long id) {
        return repository.findById(id).map(GajiKomponenResponse::from).orElse(null);
    }

    @Override
    public SavedStatus<?> create(GajiKomponenPostRequest request) {
        try {
            boolean exists = repository.exists(request.getSpecification());
            if (exists) return SavedStatus.build(ESaveStatus.DUPLICATE, "Gaji Komponen sudah ada");
            GajiProfil gajiProfil = gajiProfilRepository.findById(request.getProfilGajiId()).orElseThrow(() -> new RuntimeException("Unknown Profil Gaji"));
            GajiKomponen entity = GajiKomponenPostRequest.toEntity(request, gajiProfil);
            GajiKomponen save = repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public SavedStatus<?> update(Long id, GajiKomponenPutRequest request) {
        try {
            GajiKomponen gajiKomponen = repository.findById(id).orElseThrow(() -> new RuntimeException("Unknown Gaji Komponen"));
            GajiProfil gajiProfil = gajiProfilRepository.findById(request.getProfilGajiId()).orElseThrow(() -> new RuntimeException("Unknown Profil Gaji"));
            GajiKomponen entity = GajiKomponenPutRequest.toEntity(gajiKomponen, request, gajiProfil);
            GajiKomponen save = repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, save);
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Override
    public Boolean delete(Long id) {
        Optional<GajiKomponen> byId = repository.findById(id);
        if (byId.isEmpty()) return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        return true;
    }

    @Override
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
