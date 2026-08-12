package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfilUpdateDetail;
import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfileUpdateQuery;
import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfileUpdateRequest;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateApproval;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import id.perumdamts.kepegawaian.repositories.profil.jooq.ProfileUpdateQueryRepository;
import id.perumdamts.kepegawaian.repositories.profil.jpa.ProfileUpdateRepository;
import id.perumdamts.kepegawaian.services.revInfo.RevInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileUpdateQueryService {
    private final ProfileUpdateQueryRepository queryRepository;
    private final ProfileUpdateRepository repository;
    private final RevInfoService revInfoService;

    public Page<ProfileUpdateQuery> findPage(ProfileUpdateRequest request) {
        return queryRepository.pageQuery(request);
    }

    public ProfilUpdateDetail<?> findById(Long id) {
        Optional<ProfileUpdate> byId = repository.findById(id);
        if (byId.isEmpty() || !byId.get().getApprovalStatus().equals(EProfileUpdateApproval.PENDING)) return null;
        ProfileUpdate profileUpdate = byId.get();
        return switch (profileUpdate.getTableName()) {
            case BIODATA -> revInfoService.findBiodataRevision(profileUpdate);
            case KELUARGA -> revInfoService.findKeluargaRevision(profileUpdate);
            case PENDIDIKAN -> revInfoService.findPendidikan(profileUpdate);
            case KEAHLIAN -> revInfoService.findKeahlianRevision(profileUpdate);
            case PELATIHAN -> revInfoService.findPelatihanRevision(profileUpdate);
            case PENGALAMAN_KERJA -> revInfoService.findPengalamanKerjaRevision(profileUpdate);
            case KARTU_IDENTITAS -> revInfoService.findKartuIdentitasRevision(profileUpdate);
            case LAMPIRAN -> revInfoService.findLampiranRevision(profileUpdate);
        };
    }
}
