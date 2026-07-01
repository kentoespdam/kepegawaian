package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfilUpdateDetail;
import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfileUpdateRequest;
import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfileUpdateQuery;
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
        if (byId.get().getTableName().equals(EProfileUpdateTable.KELUARGA))
            return byId.map(revInfoService::findKeluargaRevision).orElse(null);
        return byId.map(revInfoService::findPendidikan).orElse(null);
    }
}
