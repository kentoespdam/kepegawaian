package id.perumdamts.kepegawaian.services.revInfo;

import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfilUpdateDetail;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;

public interface RevInfoService {
    ProfilUpdateDetail findKeluargaRevision(ProfileUpdate profileUpdate);

}
