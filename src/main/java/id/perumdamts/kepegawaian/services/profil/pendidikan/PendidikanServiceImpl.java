package id.perumdamts.kepegawaian.services.profil.pendidikan;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanPostRequest;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanPutRequest;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanRequest;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanResponse;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.Pendidikan;
import id.perumdamts.kepegawaian.repositories.master.JenjangPendidikanRepository;
import id.perumdamts.kepegawaian.repositories.profil.BiodataRepository;
import id.perumdamts.kepegawaian.repositories.profil.PendidikanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PendidikanServiceImpl implements PendidikanService {
    private final PendidikanRepository repository;
    private final BiodataRepository biodataRepository;
    private final JenjangPendidikanRepository jenjangPendidikanRepository;

    @Override
    public List<PendidikanResponse> findAll() {
        return repository.findAll().stream().map(PendidikanResponse::from).toList();
    }

    @Override
    public Page<PendidikanResponse> findPage(PendidikanRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(PendidikanResponse::from);
    }

    @Override
    public PendidikanResponse findById(Long id) {
        return repository.findById(id).map(PendidikanResponse::from).orElse(null);
    }

    @Override
    public List<PendidikanResponse> findByBiodataId(String biodataId) {
        return repository.findByBiodata_Nik(biodataId).stream()
                .map(PendidikanResponse::from).toList();
    }

    @Transactional
    @Override
    public SavedStatus<?> save(PendidikanPostRequest request) {
        Optional<Biodata> biodata = biodataRepository.findById(request.getBiodataId());
        if (biodata.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Biodata");

        Optional<JenjangPendidikan> jenjangPendidikan = jenjangPendidikanRepository
                .findById(request.getJenjangPendidikanId());
        if (jenjangPendidikan.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Jenjang Pendidikan");

        boolean exists = repository.exists(request.getSpecification());
        if (exists)
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Pendidikan sudah ada");

        Pendidikan pendidikan = PendidikanPostRequest.from(request, biodata.get(), jenjangPendidikan.get());
        Pendidikan save = repository.save(pendidikan);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Transactional
    @Override
    public SavedStatus<?> update(Long id, PendidikanPutRequest request) {
        Optional<Pendidikan> pendidikan = repository.findById(id);
        if (pendidikan.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Pendidikan");
        Optional<Biodata> biodata = biodataRepository.findById(request.getBiodataId());
        if (biodata.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Biodata");
        Optional<JenjangPendidikan> jenjangPendidikan = jenjangPendidikanRepository.findById(request.getJenjangPendidikanId());
        if (jenjangPendidikan.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Jenjang Pendidikan");
        Pendidikan entity = PendidikanPutRequest.from(request, pendidikan.get(), biodata.get(), jenjangPendidikan.get());
        Pendidikan save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Transactional
    @Override
    public SavedStatus<?> acceptPendidikan(Long id, String nik, String username) {
        Optional<Pendidikan> pendidikan = repository.findById(id);
        if (pendidikan.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Pendidikan");
        Pendidikan entity = pendidikan.get();
        entity.setDisetujuiOleh(username);
        entity.setTanggalDisetujui(LocalDateTime.now());
        repository.updateByBiodata_Nik(nik);
        Pendidikan save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, save);
    }

    @Transactional
    @Override
    public Boolean delete(Long id) {
        boolean exists = repository.existsById(id);
        if (!exists)
            return false;
        repository.deleteById(id);
        return true;
    }
}
