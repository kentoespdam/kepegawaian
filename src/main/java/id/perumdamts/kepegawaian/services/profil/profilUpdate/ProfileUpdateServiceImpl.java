package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfilUpdateDetail;
import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfileUpdateRequest;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateApproval;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import id.perumdamts.kepegawaian.repositories.profil.*;
import id.perumdamts.kepegawaian.services.revInfo.RevInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileUpdateServiceImpl implements ProfileUpdateService {
    private final ProfileUpdateRepository repository;
    private final RevInfoService revInfoService;
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
    public ProfilUpdateDetail findById(Long id) {
        ProfileUpdate profileUpdate = repository.findById(id).orElse(null);
        if (profileUpdate == null) return null;
        return getProfilUpdateDetail(profileUpdate);
    }

    @Override
    public void create(Long revId, RevisionMetadata.RevisionType actionType, EProfileUpdateTable tableName, String nipam, String nama, String namaJabatan) {
        ProfileUpdate entity = ProfileUpdate.builder()
                .nipam(nipam)
                .nama(nama)
                .jabatan(namaJabatan)
                .tableName(tableName)
                .actionType(actionType)
                .dataDescription(generateDescription(actionType, tableName))
                .revId(revId)
                .build();
        repository.save(entity);
    }

    @Override
    public SavedStatus<?> approval(Long id, EProfileUpdateApproval approval) {
        return null;
    }

    private ProfilUpdateDetail getProfilUpdateDetail(ProfileUpdate profileUpdate) {
        if (profileUpdate.getTableName().equals(EProfileUpdateTable.KELUARGA)) {
            return revInfoService.findKeluargaRevision(profileUpdate);
        }
        return null;
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
