package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfilUpdateDetail;
import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfileUpdateRequest;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateApproval;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.master.Jabatan;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.entities.profil.ProfilKeluarga;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import id.perumdamts.kepegawaian.repositories.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.profil.*;
import id.perumdamts.kepegawaian.services.revInfo.RevInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.data.history.Revisions;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileUpdateServiceImpl implements ProfileUpdateService {
    private final ProfileUpdateRepository repository;
    private final RevInfoService revInfoService;
    private final PegawaiRepository pegawaiRepository;
    private final BiodataRepository biodataRepository;
    private final KeahlianRepository keahlianRepository;
    private final LampiranProfilRepository lampiranProfilRepository;
    private final PelatihanRepository pelatihanRepository;
    private final PendidikanRepository pendidikanRepository;
    private final PengalamanKerjaRepository pengalamanKerjaRepository;
    private final ProfilKeluargaRepository profilKeluargaRepository;

    @Override
    public Page<ProfileUpdate> findPage(ProfileUpdateRequest request) {
        return repository.findAll(request.getSpecification(), request.getPageable());
    }

    @Override
    public Optional<ProfilUpdateDetail> findById(Long id) {
        Optional<ProfileUpdate> byId = repository.findById(id);
        return byId.flatMap(this::getProfilUpdateDetail);
    }

    @Override
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

    @Override
    public SavedStatus<?> approval(Long id, EProfileUpdateApproval approval) {
        return null;
    }

    private Optional<ProfilUpdateDetail> getProfilUpdateDetail(ProfileUpdate profileUpdate) {
        try {
            if (EProfileUpdateTable.KELUARGA.equals(profileUpdate.getTableName())) {
//                Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "id"));
                Revisions<Integer, ProfilKeluarga> revisions = profilKeluargaRepository.findRevisions(profileUpdate.getId());
                return Optional.of(ProfilUpdateDetail.from(profileUpdate, revisions));
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error getting profile update detail for id {}: {}",
                    profileUpdate.getId(), e.getMessage(), e);
            return Optional.empty();
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
}
