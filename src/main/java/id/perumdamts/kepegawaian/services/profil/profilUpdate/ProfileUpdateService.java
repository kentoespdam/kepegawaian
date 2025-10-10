package id.perumdamts.kepegawaian.services.profil.profilUpdate;

import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfilUpdateDetail;
import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfileUpdateRequest;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateApproval;
import id.perumdamts.kepegawaian.entities.commons.EProfileUpdateTable;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;
import org.springframework.data.domain.Page;
import org.springframework.data.history.RevisionMetadata;

public interface ProfileUpdateService {
    Page<ProfileUpdate> findPage(ProfileUpdateRequest request);

    ProfilUpdateDetail findById(Long id);

    void create(Long revId, RevisionMetadata.RevisionType actionType, EProfileUpdateTable tableName, String nipam, String nama, String namaJabatan);

    SavedStatus<?> approval(Long id, EProfileUpdateApproval approval);
}
