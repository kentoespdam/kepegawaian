package id.perumdamts.kepegawaian.services.auth;

import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import id.perumdamts.kepegawaian.dto.auth.AuthPostRequest;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;

import java.util.List;

public interface AuthService {
    SavedStatus<?> createUser(AuthPostRequest request);
    void createUser(Pegawai pegawai);

    SavedStatus<?> updatePref(String id, List<PrefRole> prefRoles);
}
