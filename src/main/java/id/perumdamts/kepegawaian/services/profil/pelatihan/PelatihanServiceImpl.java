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
        try {
            boolean exists = repository.exists(request.getSpecification());
            if (exists)
                return SavedStatus.build(ESaveStatus.FAILED, "Pelatihan already exists");

            Biodata biodata = biodataRepository.findById(request.getBiodataId())
                    .orElseThrow(() -> new RuntimeException("Biodata not found"));
            JenisPelatihan jenisPelatihan = jenisPelatihanRepository.findById(request.getJenisPelatihanId())
                    .orElseThrow(() -> new RuntimeException("JenisPelatihan not found"));

            Pelatihan entity = PelatihanPostRequest.toEntity(request, biodata, jenisPelatihan);
            repository.save(entity);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Pelatihan saved");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    @Override
    public SavedStatus<?> update(Long id, PelatihanPutRequest request) {
        try {
            Biodata biodata = biodataRepository.findById(request.getBiodataId())
                    .orElseThrow(() -> new RuntimeException("Biodata not found"));
            JenisPelatihan jenisPelatihan = jenisPelatihanRepository.findById(request.getJenisPelatihanId())
                    .orElseThrow(() -> new RuntimeException("JenisPelatihan not found"));
            Pelatihan entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pelatihan not found"));

            Pelatihan pelatihan = PelatihanPutRequest.toEntity(request, entity, biodata, jenisPelatihan);
            repository.save(pelatihan);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Pelatihan updated");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    @Override
    public SavedStatus<?> acceptPelatihan(Long id, String nik, String username) {
        return repository.findById(id)
                .map(entity -> {
                    entity.setDisetujui(true);
                    entity.setDisetujuiOleh(username);
                    entity.setTanggalDisetujui(LocalDateTime.now());
                    repository.save(entity);
                    return SavedStatus.build(ESaveStatus.SUCCESS, "Pelatihan accepted");
                })
                .orElse(SavedStatus.build(ESaveStatus.FAILED, "Unknown Pelatihan"));
    }

    @Transactional
    @Override
    public Boolean delete(Long id) {
        Optional<Pelatihan> byId = repository.findById(id);
        if (byId.isEmpty())
            return false;
        byId.get().setIsDeleted(true);
        repository.save(byId.get());
        lampiranProfilService.deleteByRefId(EJenisLampiranProfil.PROFIL_PELATIHAN, id);
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
