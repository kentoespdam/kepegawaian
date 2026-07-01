package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfilUpdateAcceptRequest;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateApproval;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.ProfileUpdateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileUpdateService {
    private final ProfileUpdateRepository repository;
    private final PegawaiRepository pegawaiRepository;
    private final ProfileUpdateKeluargaApprovalService approvalKeluargaService;
    private final ProfileUpdatePendidikanApprovalService approvalPendidikanService;



    public void create(Long revId, RevisionMetadata.RevisionType actionType, EProfileUpdateTable tableName) {
        AppwriteUser principal = (AppwriteUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pegawai pegawai;
        if (principal.get$id().equals("DEV")) {
            pegawai = new Pegawai();
            pegawai.setNipam("DEV");
            Biodata biodata = new Biodata();
            biodata.setNama("DEVELOP");
            pegawai.setBiodata(biodata);
            Jabatan jabatan = new Jabatan();
            jabatan.setNama("DEV");
            pegawai.setJabatan(jabatan);
        } else {
            pegawai = pegawaiRepository.findById(Long.valueOf(principal.get$id())).orElse(null);
        }

        if (pegawai == null) return;
        ProfileUpdate entity = ProfileUpdate.builder()
                .reqDate(LocalDateTime.now())
                .nipam(pegawai.getNipam())
                .nama(pegawai.getBiodata().getNama())
                .jabatan(pegawai.getJabatan().getNama())
                .tableName(tableName)
                .actionType(actionType)
                .dataDescription(generateDescription(actionType, tableName))
                .revId(revId)
                .approvalStatus(EProfileUpdateApproval.PENDING)
                .build();
        repository.save(entity);
    }

    public SavedStatus<?> approval(Long id, ProfilUpdateAcceptRequest request) {
        try {
            ProfileUpdate profileUpdate = repository.findByIdAndApprovalStatus(id, EProfileUpdateApproval.PENDING)
                    .orElseThrow(() -> new RuntimeException("Unknown Profile Update"));
            EProfileUpdateTable tableName = profileUpdate.getTableName();
            switch (tableName) {
                case KELUARGA:
                    approvalKeluargaService.changeHandler(profileUpdate, request.getApproval());
                    break;
                case PENDIDIKAN:
                    approvalPendidikanService.changeHandler(profileUpdate, request.getApproval());
                    break;
            }
            handleApproval(profileUpdate, request);
            return SavedStatus.build(ESaveStatus.SUCCESS, "Success saving approval profil update");
        } catch (Exception e) {
            log.error(e.getMessage());
            return SavedStatus.build(ESaveStatus.FAILED, e.getMessage());
        }
    }

    private String generateDescription(RevisionMetadata.RevisionType type, EProfileUpdateTable table) {
        String tableDescription = generateTableDescription(table);
        return switch (type) {
            case INSERT -> "Penambahan ";
            case UPDATE -> "Perubahan";
            case DELETE -> "Penghapusan ";
            default -> "Unknown";
        } + " " + tableDescription;
    }

    private String generateTableDescription(EProfileUpdateTable table) {
        return switch (table) {
            case BIODATA -> "data profil";
            case KELUARGA -> "data anggota keluarga";
            case PENDIDIKAN -> "data pendidikan";
            case PENGALAMAN_KERJA -> "data pengalaman kerja";
            case PELATIHAN -> "data pelatihan";
            case KEAHLIAN -> "data keahlian";
        };
    }

    private void handleApproval(ProfileUpdate entity, ProfilUpdateAcceptRequest request) {
        Pegawai pegawai = pegawaiRepository.findById(request.getPegawaiId()).orElseThrow(() -> new RuntimeException("Unknown Pic"));
        entity.setApprovalStatus(request.getApproval());
        entity.setApprovalDate(LocalDateTime.now());
        entity.setApprovalPic(pegawai.getBiodata().getNama());
        repository.save(entity);
    }

}
