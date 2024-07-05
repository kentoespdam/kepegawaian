package id.perumdamts.kepegawaian.services.profil.pendidikan;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilResponse;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.*;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.Pendidikan;
import id.perumdamts.kepegawaian.repositories.master.JenjangPendidikanRepository;
import id.perumdamts.kepegawaian.repositories.profil.BiodataRepository;
import id.perumdamts.kepegawaian.repositories.profil.PendidikanRepository;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
    private final LampiranProfilService lampiranProfilService;

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
    public Page<PendidikanResponse> findByBiodataId(String biodataId) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        return repository.findByBiodata_Nik(biodataId, pageable).map(PendidikanResponse::from);
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
        return SavedStatus.build(ESaveStatus.SUCCESS, PendidikanResponse.from(save));
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
        return SavedStatus.build(ESaveStatus.SUCCESS, PendidikanResponse.from(save));
    }

    @Transactional
    @Override
    public SavedStatus<?> acceptPendidikan(Long id, PendidikanAcceptRequest request, String username) {
        Optional<Pendidikan> pendidikan = repository.findById(id);
        if (pendidikan.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Pendidikan");
        Pendidikan entity = pendidikan.get();
        entity.setDisetujui(true);
        entity.setDisetujuiOleh(username);
        entity.setTanggalDisetujui(LocalDateTime.now());
        if (request.getIsLatest())
            repository.updateByBiodata_Nik(request.getBiodataId());
        Pendidikan save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, PendidikanResponse.from(save));
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

    //lampiran
    @Override
    public List<LampiranProfilResponse> getLampiran(Long id) {
        return lampiranProfilService.getLampiran(EJenisLampiranProfil.PROFIL_PENDIDIKAN, id);
    }

    @Override
    public LampiranProfilResponse getLampiranById(Long id) {
        return lampiranProfilService.getLampiranById(id);
    }

    @Override
    public ResponseEntity<?> getFileLampiranById(Long id) {
        return lampiranProfilService.getFileLampiranById(EJenisLampiranProfil.PROFIL_PENDIDIKAN, id);
    }

    @Transactional
    @Override
    public SavedStatus<?> addLampiran(PendidikanLampiranPostRequest request) {
        boolean exists = repository.existsById(request.getRefId());
        if (!exists)
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Kartu Identitas");

        return lampiranProfilService.addLampiran(request);
    }

    @Override
    public Boolean deleteLampiran(Long id) {
        return lampiranProfilService.deleteById(id);
    }

    @Override
    public void saveFromBio(Biodata save, JenjangPendidikan jenjangPendidikan) {
        Pendidikan pendidikan = new Pendidikan();
        pendidikan.setBiodata(save);
        pendidikan.setJenjangPendidikan(jenjangPendidikan);
        repository.save(pendidikan);
    }
}
