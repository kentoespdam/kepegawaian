package id.perumdamts.kepegawaian.services.revInfo;

import id.perumdamts.kepegawaian.dto.profil.keluarga.ProfilKeluargaResponse;
import id.perumdamts.kepegawaian.dto.profil.pendidikan.PendidikanResponse;
import id.perumdamts.kepegawaian.dto.profil.profileUpdate.ProfilUpdateDetail;
import id.perumdamts.kepegawaian.entities.profil.ProfileUpdate;

import java.util.List;

public interface RevInfoService {
    ProfilUpdateDetail<ProfilKeluargaResponse> findKeluargaRevision(ProfileUpdate profileUpdate);

    ProfilUpdateDetail<PendidikanResponse> findPendidikan(ProfileUpdate profileUpdate);

    <T> List<T> findLatestRevision(Class<T> entityClass, Long entityId);
}
