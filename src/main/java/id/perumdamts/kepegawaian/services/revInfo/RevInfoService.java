package id.perumdamts.kepegawaian.services.revInfo;

import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaResponse;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanResponse;
import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfilUpdateDetail;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;

public interface RevInfoService {
    ProfilUpdateDetail<ProfilKeluargaResponse> findKeluargaRevision(ProfileUpdate profileUpdate);
    ProfilUpdateDetail<PendidikanResponse> findPendidikan(ProfileUpdate profileUpdate);

}
