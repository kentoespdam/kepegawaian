package id.perumdamts.kepegawaian.services.profil.pelatihan;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilResponse;
import id.perumdamts.kepegawaian.dto.profil.pelatihan.*;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.master.JenisPelatihan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.Pelatihan;
import id.perumdamts.kepegawaian.repositories.master.JenisPelatihanRepository;
import id.perumdamts.kepegawaian.repositories.profil.BiodataRepository;
import id.perumdamts.kepegawaian.repositories.profil.PelatihanRepository;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PelatihanServiceImpl implements PelatihanService {
    private final PelatihanRepository repository;
    private final BiodataRepository biodataRepository;
    private final JenisPelatihanRepository jenisPelatihanRepository;
    private final LampiranProfilService lampiranProfilService;

    @Override
    public List<PelatihanResponse> findAll() {
        return repository.findAll().stream().map(PelatihanResponse::from).toList();
    }

    @Override
    public Page<PelatihanResponse> findPage(PelatihanRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(PelatihanResponse::from);
    }

    @Override
    public PelatihanResponse findById(Long id) {
        return repository.findById(id).map(PelatihanResponse::from).orElse(null);
    }

    @Override
    public Page<PelatihanResponse> findByBiodataId(String biodataId, PelatihanRequest request) {
        request.setBiodataId(biodataId);
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(PelatihanResponse::from);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(PelatihanPostRequest request) {
        Optional<Biodata> biodata = biodataRepository.findById(request.getBiodataId());
        if (biodata.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Biodata");

        Optional<JenisPelatihan> jenisPelatihan = jenisPelatihanRepository.findById(request.getJenisPelatihanId());
        if (jenisPelatihan.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Jenis Pelatihan");

        boolean exists = repository.exists(request.getSpecification());
        if (exists)
            return SavedStatus.build(ESaveStatus.FAILED, "Pelatihan already exists");

        Pelatihan entity = PelatihanPostRequest.toEntity(request, biodata.get(), jenisPelatihan.get());
        Pelatihan save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, PelatihanResponse.from(save));
    }

    @Transactional
    @Override
    public SavedStatus<?> update(Long id, PelatihanPutRequest request) {
        Optional<Biodata> biodata = biodataRepository.findById(request.getBiodataId());
        if (biodata.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Biodata");

        Optional<JenisPelatihan> jenisPelatihan = jenisPelatihanRepository.findById(request.getJenisPelatihanId());
        if (jenisPelatihan.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Jenis Pelatihan");

        Optional<Pelatihan> entity = repository.findById(id);
        if (entity.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Pelatihan");

        Pelatihan pelatihan = PelatihanPutRequest.toEntity(request, entity.get(), biodata.get(), jenisPelatihan.get());
        Pelatihan save = repository.save(pelatihan);
        return SavedStatus.build(ESaveStatus.SUCCESS, PelatihanResponse.from(save));
    }

    @Transactional
    @Override
    public SavedStatus<?> acceptPelatihan(Long id, String nik, String username) {
        Optional<Pelatihan> pelatihan = repository.findById(id);
        if (pelatihan.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Pelatihan");
        Pelatihan entity = pelatihan.get();
        entity.setDisetujui(true);
        entity.setDisetujuiOleh(username);
        entity.setTanggalDisetujui(LocalDateTime.now());
        Pelatihan save = repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, PelatihanResponse.from(save));
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

    // Lampiran
    @Override
    public List<LampiranProfilResponse> getLampiran(Long id) {
        return lampiranProfilService.getLampiran(EJenisLampiranProfil.PROFIL_PELATIHAN, id);
    }

    @Override
    public LampiranProfilResponse getLampiranDetail(Long id) {
        return lampiranProfilService.getLampiranById(id);
    }

    @Override
    public ResponseEntity<?> getFileLampiranById(Long id) {
        return lampiranProfilService.getFileLampiranById(EJenisLampiranProfil.PROFIL_PELATIHAN, id);
    }

    @Override
    public SavedStatus<?> addLampiran(PelatihanLampiranPostRequest request) {
        boolean exists = repository.existsById(request.getRefId());
        if (!exists)
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Pelatihan");
        return lampiranProfilService.addLampiran(request);
    }

    @Override
    public Boolean deleteLampiran(Long id) {
        return lampiranProfilService.deleteById(id);
    }
}
