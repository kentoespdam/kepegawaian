package id.perumdamts.kepegawaian.services.profil.keluarga;

import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaPutRequest;
import id.perumdamts.kepegawaian.entities.master.JenjangPendidikan;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.ProfilKeluarga;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.mapper.profil.keluarga.ProfilKeluargaMapper;
import id.perumdamts.kepegawaian.repositories.master.jpa.JenjangPendidikanRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.BiodataRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.ProfilKeluargaRepository;
import id.perumdamts.kepegawaian.services.profil.profilUpdate.ProfileUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfilKeluargaCommandService {
    private static final String UNKNOWN_BIODATA = "Unknown Biodata";
    private static final String UNKNOWN_KELUARGA = "Unknown Profil Keluarga";
    private static final String UNKNOWN_JENJANG = "Unknown Jenjang Pendidikan";

    private final ProfilKeluargaRepository repository;
    private final BiodataRepository biodataRepository;
    private final JenjangPendidikanRepository jenjangPendidikanRepository;
    private final ProfileUpdateService profileUpdateService;

    @Transactional
    public SavedStatus<Long> create(ProfilKeluargaPostRequest request, boolean requiresApproval) {
        Biodata biodata = biodataRepository.findById(request.getBiodataId()).orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));
        repository.findActiveByBiodataIdAndNamaAndTanggalLahir(request.getBiodataId(), request.getNama(), request.getTanggalLahir())
                .ifPresent(e -> { throw new ConflictException("Profil Keluarga aktif dengan nama dan tanggal lahir sama sudah ada"); });

        JenjangPendidikan jenjangPendidikan = resolveJenjangPendidikan(request.getPendidikanId());
        ProfilKeluarga entity = ProfilKeluargaMapper.toEntity(request, biodata, jenjangPendidikan);
        entity.setChangedStatus(requiresApproval);
        ProfilKeluarga saved = repository.save(entity);
        handleRevisionUpdate(saved, RevisionMetadata.RevisionType.INSERT);
        return SavedStatus.build(ESaveStatus.SUCCESS, saved.getId());
    }

    @Transactional
    public SavedStatus<Long> update(Long id, ProfilKeluargaPutRequest request, boolean requiresApproval) {
        ProfilKeluarga entity = repository.findById(id).orElseThrow(() -> new NotFoundException(UNKNOWN_KELUARGA));
        if (Boolean.TRUE.equals(entity.getChangedStatus())) {
            throw new ConflictException("Data Keluarga sedang dalam proses pengajuan");
        }

        boolean biodataChanged = !entity.getBiodata().getNik().equals(request.getBiodataId());
        boolean namaChanged = !entity.getNama().equals(request.getNama());
        boolean tanggalLahirChanged = !entity.getTanggalLahir().equals(request.getTanggalLahir());
        if (biodataChanged || namaChanged || tanggalLahirChanged) {
            repository.findActiveByBiodataIdAndNamaAndTanggalLahir(request.getBiodataId(), request.getNama(), request.getTanggalLahir())
                    .ifPresent(e -> {
                        if (!e.getId().equals(id)) {
                            throw new ConflictException("Profil Keluarga lain dengan nama dan tanggal lahir yang sama sudah ada");
                        }
                    });
        }

        Biodata biodata = biodataRepository.findById(request.getBiodataId()).orElseThrow(() -> new NotFoundException(UNKNOWN_BIODATA));
        JenjangPendidikan jenjangPendidikan = resolveJenjangPendidikan(request.getPendidikanId());
        ProfilKeluarga updated = ProfilKeluargaMapper.updateEntity(entity, request, jenjangPendidikan);
        updated.setBiodata(biodata);
        updated.setChangedStatus(requiresApproval);
        ProfilKeluarga saved = repository.save(updated);
        handleRevisionUpdate(saved, RevisionMetadata.RevisionType.UPDATE);
        return SavedStatus.build(ESaveStatus.SUCCESS, saved.getId());
    }

    @Transactional
    public boolean delete(Long id, boolean requiresApproval) {
        ProfilKeluarga entity = repository.findById(id).orElseThrow(() -> new NotFoundException(UNKNOWN_KELUARGA));
        entity.setIsDeleted(true);
        entity.setChangedStatus(requiresApproval);
        ProfilKeluarga saved = repository.save(entity);
        handleRevisionUpdate(saved, RevisionMetadata.RevisionType.DELETE);
        return true;
    }

    private void handleRevisionUpdate(ProfilKeluarga saved, RevisionMetadata.RevisionType type) {
        if (Boolean.FALSE.equals(saved.getChangedStatus())) return;
        profileUpdateService.create(String.valueOf(saved.getId()), type, EProfileUpdateTable.KELUARGA);
    }

    private JenjangPendidikan resolveJenjangPendidikan(Long pendidikanId) {
        if (pendidikanId == null || pendidikanId == 0L) return null;
        return jenjangPendidikanRepository.findById(pendidikanId).orElseThrow(() -> new NotFoundException(UNKNOWN_JENJANG));
    }
}