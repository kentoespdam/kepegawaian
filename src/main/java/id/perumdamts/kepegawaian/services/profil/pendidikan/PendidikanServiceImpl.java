package id.perumdamts.kepegawaian.services.profil.pendidikan;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilResponse;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.*;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.Pendidikan;
import id.perumdamts.kepegawaian.repositories.master.JenjangPendidikanRepository;
import id.perumdamts.kepegawaian.repositories.profil.BiodataRepository;
import id.perumdamts.kepegawaian.repositories.profil.PendidikanRepository;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilService;
import id.perumdamts.kepegawaian.services.profil.profilUpdate.ProfileUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PendidikanServiceImpl implements PendidikanService {
    private static final String UNKNOWN_BIODATA = "Unknown Biodata";
    private static final String UNKNOWN_PENDIDIKAN = "Unknown Pendidikan";
    private static final String UNKNOWN_JENJANG_PENDIDIKAN = "Unknown Jenjang Pendidikan";

    private final PendidikanRepository repository;
    private final BiodataRepository biodataRepository;
    private final JenjangPendidikanRepository jenjangPendidikanRepository;
    private final LampiranProfilService lampiranProfilService;
    private final ProfileUpdateService profileUpdateService;

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
    public Page<PendidikanResponse> findByBiodataId(String biodataId, PendidikanRequest request) {
        request.setBiodataId(biodataId);
        return repository.findAll(request.getSpecification(), request.getPageable()).map(PendidikanResponse::from);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(PendidikanPostRequest request) {
        Optional<Biodata> biodata = biodataRepository.findById(request.getBiodataId());
        if (biodata.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, UNKNOWN_BIODATA);

        Optional<JenjangPendidikan> jenjangPendidikan = jenjangPendidikanRepository
                .findById(request.getJenjangPendidikanId());
        if (jenjangPendidikan.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, UNKNOWN_JENJANG_PENDIDIKAN);

        boolean exists = repository.exists(request.getSpecification());
        if (exists)
            return SavedStatus.build(ESaveStatus.DUPLICATE, "Pendidikan sudah ada");

        Pendidikan pendidikan = PendidikanPostRequest.from(request, biodata.get(), jenjangPendidikan.get());
        Pendidikan save = repository.save(pendidikan);
        handleUpdateIsLatest(request.getIsLatest(), save.getId(), biodata.get(), jenjangPendidikan.get());
        handleRevisionUpdate(save, RevisionMetadata.RevisionType.INSERT);
        return SavedStatus.build(ESaveStatus.SUCCESS, PendidikanResponse.from(save));
    }

    @Transactional
    @Override
    public SavedStatus<?> update(Long id, PendidikanPutRequest request) {
        Optional<Pendidikan> pendidikan = repository.findById(id);
        if (pendidikan.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, UNKNOWN_PENDIDIKAN);
        Optional<Biodata> biodata = biodataRepository.findById(request.getBiodataId());
        if (biodata.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, UNKNOWN_BIODATA);
        Optional<JenjangPendidikan> jenjangPendidikan = jenjangPendidikanRepository.findById(request.getJenjangPendidikanId());
        if (jenjangPendidikan.isEmpty())
            return SavedStatus.build(ESaveStatus.FAILED, UNKNOWN_JENJANG_PENDIDIKAN);
        Pendidikan entity = PendidikanPutRequest.from(request, pendidikan.get(), biodata.get(), jenjangPendidikan.get());
        Pendidikan save = repository.save(entity);
        handleUpdateIsLatest(request.getIsLatest(), save.getId(), biodata.get(), jenjangPendidikan.get());
        handleRevisionUpdate(save, RevisionMetadata.RevisionType.UPDATE);
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
        return repository.findById(id)
                .map(entity -> {
                    entity.setIsDeleted(true);
                    entity.setChangedStatus(true);
                    repository.save(entity);

                    handleRevisionUpdate(entity, RevisionMetadata.RevisionType.DELETE);
                    lampiranProfilService.deleteByRefId(EJenisLampiranProfil.PROFIL_PENDIDIKAN, id);
                    return true;
                })
                .orElse(false);
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
        pendidikan.setIsLatest(true);
        repository.save(pendidikan);
    }

    private void handleUpdateIsLatest(Boolean isLatest, Long id, Biodata biodata, JenjangPendidikan jenjangPendidikan) {
        if (Boolean.FALSE.equals(isLatest)) return;

        repository.updateIsLatest(id, biodata.getNik());
        biodata.setPendidikanTerakhir(jenjangPendidikan);
        biodataRepository.save(biodata);
    }

    private void handleRevisionUpdate(Pendidikan save, RevisionMetadata.RevisionType type) {
        if (Boolean.FALSE.equals(save.getChangedStatus())) return;

        profileUpdateService.create(
                save.getId(),
                type,
                EProfileUpdateTable.PENDIDIKAN
        );
    }
}
