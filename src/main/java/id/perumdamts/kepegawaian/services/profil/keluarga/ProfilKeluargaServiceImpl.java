package id.perumdamts.kepegawaian.services.profil.keluarga;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.keluarga.*;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilResponse;
import id.perumdamts.kepegawaian.entities.commons.EHubunganKeluarga;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.ProfilKeluarga;
import id.perumdamts.kepegawaian.repositories.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenjangPendidikanRepository;
import id.perumdamts.kepegawaian.repositories.profil.BiodataRepository;
import id.perumdamts.kepegawaian.repositories.profil.ProfilKeluargaRepository;
import id.perumdamts.kepegawaian.services.profil.ChangedStatusResolver;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilService;
import id.perumdamts.kepegawaian.services.profil.profilUpdate.ProfileUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfilKeluargaServiceImpl implements ProfilKeluargaService {
    private static final String UNKNOWN_BIODATA = "Unknown Biodata";
    private static final String UNKNOWN_PROFIL_KELUARGA = "Unknown Profil Keluarga";
    private static final String UPDATE_FAILED_STATUS = "Failed Update karena dalam proses menunggu persetujuan";

    private final ProfilKeluargaRepository repository;
    private final BiodataRepository biodataRepository;
    private final JenjangPendidikanRepository jenjangPendidikanRepository;
    private final LampiranProfilService lampiranProfilService;
    private final PegawaiRepository pegawaiRepository;
    private final ProfileUpdateService profileUpdateService;
    private final ChangedStatusResolver resolver;

    // Predicate untuk mengecek hubungan keluarga yang bukan pasangan
    private final Predicate<EHubunganKeluarga> isNonPasanganPredicate =
            hubungan -> !EHubunganKeluarga.ISTRI.equals(hubungan)
                    && !EHubunganKeluarga.SUAMI.equals(hubungan);


    @Override
    public List<ProfilKeluargaResponse> findAll() {
        return repository.findAll().stream()
                .map(ProfilKeluargaResponse::from).toList();
    }

    @Override
    public Page<ProfilKeluargaResponse> findPage(ProfilKeluargaRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(ProfilKeluargaResponse::from);
    }

    @Override
    public Optional<ProfilKeluargaResponse> findById(Long id) {
        return repository.findById(id).map(ProfilKeluargaResponse::from);
    }

    @Override
    public Page<ProfilKeluargaResponse> findByBiodataId(String biodataId, ProfilKeluargaRequest request) {
        request.setBiodataId(biodataId);
        System.out.println(request);
        return repository.findAll(request.getSpecification(), request.getPageable())
                .map(ProfilKeluargaResponse::from);
    }

    @Transactional
    @Override
    public SavedStatus<?> save(ProfilKeluargaPostRequest request) {
        try {
            if (repository.exists(request.getSpecification()))
                return SavedStatus.build(ESaveStatus.DUPLICATE, "Profil Keluarga sudah ada");

            Biodata biodata = biodataRepository.findById(request.getBiodataId())
                    .orElseThrow(() -> new IllegalArgumentException(UNKNOWN_BIODATA));

            JenjangPendidikan jenjangPendidikan = jenjangPendidikanRepository.findById(request.getPendidikanId())
                    .orElse(null);

            ProfilKeluarga entity = ProfilKeluargaPostRequest.toEntity(request, biodata, jenjangPendidikan);
            entity.setChangedStatus(resolver.requiresApproval());

            ProfilKeluarga saved = repository.save(entity);
            handlePostSaveOperations(request, saved);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Data Keluarga Berhasil disimpan");
        } catch (Exception e) {
            log.error("Error saving ProfilKeluarga: {}", e.getMessage(), e);
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    @Override
    public SavedStatus<?> update(Long id, ProfilKeluargaPutRequest request) {
        try {
            ProfilKeluarga profilKeluarga = repository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException(UNKNOWN_PROFIL_KELUARGA));

            if (Boolean.TRUE.equals(profilKeluarga.getChangedStatus())) {
                throw new IllegalStateException(UPDATE_FAILED_STATUS);
            }

            JenjangPendidikan jenjangPendidikan = jenjangPendidikanRepository
                    .findById(request.getPendidikanId()).orElse(null);

            ProfilKeluarga entity = ProfilKeluargaPutRequest
                    .toEntity(request, profilKeluarga, jenjangPendidikan);
            entity.setChangedStatus(resolver.requiresApproval());

            ProfilKeluarga saved = repository.save(entity);
            handlePostUpdateOperations(request, saved);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Update Profil Keluarga berhasil");
        } catch (Exception e) {
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    @Transactional
    @Override
    public Boolean delete(Long id) {
        return repository.findById(id)
                .map(entity -> {
                    entity.setIsDeleted(true);
                    entity.setChangedStatus(resolver.requiresApproval());
                    repository.save(entity);

                    // Execute cleanup operations
                    executeDeleteOperations(entity);
                    return true;
                })
                .orElse(false);
    }

    //lampiran
    @Override
    public List<LampiranProfilResponse> getLampiran(Long id) {
        return lampiranProfilService.getLampiran(EJenisLampiranProfil.PROFIL_KELUARGA, id);
    }

    @Override
    public Optional<LampiranProfilResponse> getLampiranById(Long id) {
        return Optional.ofNullable(lampiranProfilService.getLampiranById(id));
    }

    @Override
    public ResponseEntity<?> getFileLampiranById(Long id) {
        return lampiranProfilService.getFileLampiranById(EJenisLampiranProfil.PROFIL_KELUARGA, id);
    }

    @Transactional
    @Override
    public SavedStatus<?> addLampiran(ProfilKeluargaLampiranPostRequest request) {
        if (!repository.existsById(request.getRefId())) {
            return SavedStatus.build(ESaveStatus.FAILED, "Unknown Kartu Identitas");
        }
        return lampiranProfilService.addLampiran(request);
    }

    @Override
    public Boolean deleteLampiran(Long id) {
        return lampiranProfilService.deleteById(id);
    }

    @Override
    public Object getRevisionPage(Long id) {
        Pageable pageable = PageRequest.of(0, 1);
        Page<Revision<Integer, ProfilKeluarga>> revisions = repository.findRevisions(id, pageable);
        return revisions.getContent().getLast().getEntity();
    }

    // Private helper methods
    private void handlePostSaveOperations(ProfilKeluargaPostRequest request, ProfilKeluarga savedEntity) {
        // Update tanggungan jika memenuhi kondisi
        if (isNonPasanganPredicate.test(request.getHubunganKeluarga()) &&
                Boolean.TRUE.equals(savedEntity.getTanggungan())) {
            updateTanggunganPegawai(request.getBiodataId());
        }

        // Create profile update record if changed
        if (Boolean.TRUE.equals(savedEntity.getChangedStatus())) {
            profileUpdateService.create(
                    savedEntity.getId(),
                    RevisionMetadata.RevisionType.INSERT,
                    EProfileUpdateTable.KELUARGA
            );
        }
    }

    private void handlePostUpdateOperations(ProfilKeluargaPutRequest request, ProfilKeluarga savedEntity) {
        log.debug("Changed status after update: {}", savedEntity.getChangedStatus());

        // Create profile update record if changed
        if (Boolean.TRUE.equals(savedEntity.getChangedStatus())) {
            profileUpdateService.create(
                    savedEntity.getId(),
                    RevisionMetadata.RevisionType.UPDATE,
                    EProfileUpdateTable.KELUARGA
            );
        }

        // Update tanggungan jika memenuhi kondisi
        if (isNonPasanganPredicate.test(request.getHubunganKeluarga()) &&
                Boolean.TRUE.equals(savedEntity.getTanggungan())) {
            updateTanggunganPegawai(request.getBiodataId());
        }
    }

    private void executeDeleteOperations(ProfilKeluarga profilKeluarga) {
        // Delete lampiran
        lampiranProfilService.deleteByRefId(EJenisLampiranProfil.PROFIL_KELUARGA, profilKeluarga.getId());

        // Update tanggungan
        updateTanggunganPegawai(profilKeluarga.getNik());

        // Create profile update record
        profileUpdateService.create(
                profilKeluarga.getId(),
                RevisionMetadata.RevisionType.DELETE, // Changed from INSERT to DELETE for consistency
                EProfileUpdateTable.KELUARGA
        );
    }

    private void updateTanggunganPegawai(String nik) {
        Specification<ProfilKeluarga> specification = (root, query, cb) -> cb.and(
                cb.equal(root.get("biodata").get("nik"), nik),
                cb.equal(root.get("tanggungan"), true),
                cb.equal(root.get("isDeleted"), false) // Added to exclude deleted records
        );

        try {
            pegawaiRepository.findByBiodata_Nik(nik).ifPresent(pegawai -> {
                long count = repository.count(specification);
                pegawai.setJmlTanggungan((int) count);
                pegawaiRepository.save(pegawai);
                log.debug("Updated tanggungan for pegawai with nik {}: {}", nik, count);
            });
        } catch (Exception e) {
            log.error("Error updating tanggungan for nik {}: {}", nik, e.getMessage(), e);
            throw new RuntimeException("Failed to update tanggungan", e);
        }
    }

}
