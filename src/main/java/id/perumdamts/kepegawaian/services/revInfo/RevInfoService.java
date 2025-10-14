package id.perumdamts.kepegawaian.services.revInfo;

import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfilUpdateDetail;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;

import java.util.Optional;

public interface RevInfoService {
    Optional<ProfilUpdateDetail> findKeluargaRevision(ProfileUpdate profileUpdate);

}
