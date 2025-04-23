package id.perumdamts.kepegawaian.services.auth;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import id.perumdamts.kepegawaian.dto.auth.AuthPostRequest;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.users.UserPatchStatusRequest;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;

import java.util.List;

public interface AuthService {
    AppwriteUser getUser(String id);
    SavedStatus<?> createUser(AuthPostRequest request);
    void createUser(Pegawai pegawai);
    AppwriteUser updateStatus(String id, UserPatchStatusRequest status);

    SavedStatus<?> updatePref(String id, List<PrefRole> prefRoles);
}
