package id.perumdamts.kepegawaian.services.profil.keluarga;

import id.perumdamts.kepegawaian.mapper.profil.keluarga.ProfilKeluargaMapper;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaLampiranPostRequest;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaPostRequest;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaPutRequest;
import id.perumdamts.kepegawaian.dto.profil.lampiranProfil.LampiranProfilResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisLampiranProfil;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.ProfilKeluarga;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenjangPendidikanRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.BiodataRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.ProfilKeluargaRepository;
import id.perumdamts.kepegawaian.services.profil.ChangedStatusResolver;
import id.perumdamts.kepegawaian.services.profil.lampiranProfil.LampiranProfilService;
import id.perumdamts.kepegawaian.services.profil.profilUpdate.ProfileUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfilKeluargaCommandService {
    private static final String UNKNOWN_BIODATA = "Unknown Biodata";
    private static final String UNKNOWN_KELUARGA = "Unknown Profil Keluarga";
    private static final String UNKNOWN_JENJANG = "Unknown Jenjang Pendidikan";

    private final ProfilKeluargaRepository repository;
    private final BiodataRepository biodataRepository;
    private final JenjangPendidikanRepository jenjangPendidikanRepository;
    private final ChangedStatusResolver resolver;
    private final ProfileUpdateService profileUpdateService;
    private final LampiranProfilService lampiranProfilService;

    @Transactional
    public SavedStatus<?> create(ProfilKeluargaPostRequest request) {
        Biodata biodata = biodataRepository.findById(request.getBiodataId())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));

        // Reject only exact active duplicates (same biodata + nama + tanggalLahir where isDeleted=false)
        repository.findActiveByBiodataIdAndNamaAndTanggalLahir(
                        request.getBiodataId(), request.getNama(), request.getTanggalLahir())
                .ifPresent(e -> {
                    throw new ConflictException("Profil Keluarga aktif dengan nama dan tanggal lahir sama sudah ada");
                });

        JenjangPendidikan jenjangPendidikan = resolveJenjangPendidikan(request.getPendidikanId());

        ProfilKeluarga entity = ProfilKeluargaMapper.toEntity(request, biodata, jenjangPendidikan);
        entity.setChangedStatus(resolver.requiresApproval());

        repository.save(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, "Data Keluarga Berhasil disimpan");
    }

    @Transactional
    public SavedStatus<?> update(Long id, ProfilKeluargaPutRequest request) {
        ProfilKeluarga entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_KELUARGA));

        if (Boolean.TRUE.equals(entity.getChangedStatus())) {
            return SavedStatus.build(ESaveStatus.FAILED, "Data Keluarga sedang dalam proses pengajuan");
        }

        // If biodata/nama/tanggalLahir changed, check no OTHER active row matches the new combo
        boolean biodataChanged = !entity.getBiodata().getNik().equals(request.getBiodataId());
        boolean namaChanged = !entity.getNama().equals(request.getNama());
        boolean tanggalLahirChanged = !entity.getTanggalLahir().equals(request.getTanggalLahir());
        if (biodataChanged || namaChanged || tanggalLahirChanged) {
            repository.findActiveByBiodataIdAndNamaAndTanggalLahir(
                            request.getBiodataId(), request.getNama(), request.getTanggalLahir())
                    .ifPresent(e -> {
                        if (!e.getId().equals(id)) {
                            throw new ConflictException("Profil Keluarga lain dengan nama dan tanggal lahir yang sama sudah ada");
                        }
                    });
        }

        Biodata biodata = biodataRepository.findById(request.getBiodataId())
                .orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));

        JenjangPendidikan jenjangPendidikan = resolveJenjangPendidikan(request.getPendidikanId());

        ProfilKeluarga updated = ProfilKeluargaMapper.updateEntity(entity, request, jenjangPendidikan);
        updated.setBiodata(biodata);
        updated.setChangedStatus(resolver.requiresApproval());

        repository.save(updated);
        return SavedStatus.build(ESaveStatus.SUCCESS, "Data Keluarga Berhasil diperbaharui");
    }

    @Transactional
    public SavedStatus<?> delete(Long id) {
        ProfilKeluarga entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_KELUARGA));
        repository.delete(entity);
        return SavedStatus.build(ESaveStatus.SUCCESS, "Data Keluarga Berhasil dihapus");
    }

    // Lampiran delegates

    public List<LampiranProfilResponse> getLampiran(Long id) {
        return lampiranProfilService.getLampiran(EJenisLampiranProfil.PROFIL_KELUARGA, id);
    }

    public LampiranProfilResponse getLampiranById(Long id) {
        return lampiranProfilService.getLampiranById(id);
    }

    public ResponseEntity<?> getFileLampiranById(Long id) {
        return lampiranProfilService.getFileLampiranById(EJenisLampiranProfil.PROFIL_KELUARGA, id);
    }

    @Transactional
    public Long addLampiran(ProfilKeluargaLampiranPostRequest request) {
        boolean exists = repository.existsById(request.getRefId());
        if (!exists)
            throw new NotFoundException(UNKNOWN_KELUARGA);
        lampiranProfilService.addLampiran(request);
        return request.getRefId();
    }

    @Transactional
    public void deleteLampiran(Long id) {
        lampiranProfilService.deleteById(id);
    }

    // Private helpers

    private JenjangPendidikan resolveJenjangPendidikan(Long pendidikanId) {
        if (pendidikanId == null || pendidikanId == 0L) return null;
        return jenjangPendidikanRepository.findById(pendidikanId)
                .orElseThrow(() -> new NotFoundException(UNKNOWN_JENJANG));
    }

    private void handleRevisionUpdate(ProfilKeluarga save, RevisionMetadata.RevisionType type) {
        if (Boolean.FALSE.equals(save.getChangedStatus())) return;
        profileUpdateService.create(save.getId(), type, EProfileUpdateTable.KELUARGA);
    }
}