package id.perumdamts.kepegawaian.services.profil.keahlian;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianPostRequest;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianPutRequest;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianRequest;
import id.perumdamts.kepegawaian.dto.profil.keahlian.KeahlianResponse;
import id.perumdamts.kepegawaian.entities.master.JenisKeahlian;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.Keahlian;
import id.perumdamts.kepegawaian.repositories.master.JenisKeahlianRepository;
import id.perumdamts.kepegawaian.repositories.profil.BiodataRepository;
import id.perumdamts.kepegawaian.repositories.profil.KeahlianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KeahlianServiceImpl implements KeahlianService {
    private final KeahlianRepository repository;
    private final BiodataRepository biodataRepository;
    private final JenisKeahlianRepository jenisKeahlianRepository;

    @Override
    public List<KeahlianResponse> findAll() {
        return repository.findAll().stream().map(KeahlianResponse::from).toList();
    }

    @Override
    public Page<KeahlianResponse> findPage(KeahlianRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(KeahlianResponse::from);
    }

    @Override
    public KeahlianResponse findById(Long id) {
        return repository.findById(id).map(KeahlianResponse::from).orElse(null);
    }

    @Override
    public List<KeahlianResponse> findByBiodataId(String biodataId) {
        return repository.findByBiodata_Nik(biodataId).stream().map(KeahlianResponse::from).toList();
    }

    @Override
    public SavedStatus<?> save(KeahlianPostRequest request) {
        Optional<Biodata> biodata = biodataRepository.findById(request.getBiodataId());
        if (biodata.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Biodata");

        Optional<JenisKeahlian> jenisKeahlian = jenisKeahlianRepository.findById(request.getKeahlianId());
        if (jenisKeahlian.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Jenis Keahlian");

        boolean exists = repository.exists(request.getSpecification());
        if (exists)
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Keahlian sudah ada");

        Keahlian entity = KeahlianPostRequest.toEntity(request, biodata.get(), jenisKeahlian.get());
        Keahlian save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, KeahlianResponse.from(save));
    }

    @Override
    public SavedStatus<?> update(Long id, KeahlianPutRequest request) {
        Optional<Biodata> biodata = biodataRepository.findById(request.getBiodataId());
        if (biodata.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Biodata");

        Optional<JenisKeahlian> jenisKeahlian = jenisKeahlianRepository.findById(request.getKeahlianId());
        if (jenisKeahlian.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Jenis Keahlian");

        Optional<Keahlian> entity = repository.findById(id);
        if (entity.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Keahlian");

        Keahlian keahlian = KeahlianPutRequest.toEntity(request, entity.get(), biodata.get(), jenisKeahlian.get());
        Keahlian save = repository.save(keahlian);
        return SavedStatus.build(ESaveStatus.SUCCESS, KeahlianResponse.from(save));
    }

    @Override
    public SavedStatus<?> acceptKeahlian(Long id, String nik, String username) {
        Optional<Keahlian> keahlian = repository.findById(id);
        if (keahlian.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Keahlian");
        Keahlian entity = keahlian.get();
        entity.setDisetujui(true);
        entity.setDisetujuiOleh(username);
        entity.setTanggalDisetujui(LocalDateTime.now());
        Keahlian save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, KeahlianResponse.from(save));
    }

    @Override
    public Boolean delete(Long id) {
        boolean exists = repository.existsById(id);
        if (!exists)
            return false;
        repository.deleteById(id);
        return true;
    }
}
