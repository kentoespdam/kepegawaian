package id.perumdamts.kepegawaian.services.auth;

import id.perumdamts.kepegawaian.config.appwrite.AppwriteClient;
import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUserPostRequest;
import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import id.perumdamts.kepegawaian.dto.auth.AuthPostRequest;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.users.UserPatchStatusRequest;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final AppwriteClient appwriteClient;

    public AppwriteUser getUser(String id) {
        return appwriteClient.getUser(id);
    }

    public SavedStatus<String> createUser(AuthPostRequest request) {
        AppwriteUserPostRequest user = AppwriteUserPostRequest.builder()
                .userId(request.getId())
                .email(request.getNipam() + "@perumdamts.com")
                .password(request.getPassword())
                .name(request.getNama())
                .build();

        appwriteClient.createUser(user);

        if (!request.getRoles().isEmpty())
            appwriteClient.updatePrefs(request.getId(), request.getRoles());

        return SavedStatus.build(ESaveStatus.SUCCESS, "success");
    }

    public void createUser(Pegawai pegawai) {
        appwriteClient.createUserWithDefaultRoles(
                pegawai.getId().toString(),
                pegawai.getNipam() + "@perumdamts.com",
                "tirtasatria",
                pegawai.getBiodata().getNama()
        );
    }

    public AppwriteUser updateStatus(String id, UserPatchStatusRequest status) {
        return appwriteClient.updateStatus(id, status);
    }

    /**
     * ADR-0039: disable Appwrite user (status blocked) secara best-effort.
     * Dipanggil saat pegawai terminasi ({@code BERHENTI_OR_KELUAR}) atau di-hard-delete —
     * user tidak pernah di-hard-delete, lifecycle-nya mengikuti pegawai.
     * Kegagalan hanya di-log, tidak menggagalkan transaksi utama.
     */
    public void blockUserIfExists(String userId) {
        try {
            UserPatchStatusRequest request = new UserPatchStatusRequest();
            request.setStatus(true);
            appwriteClient.updateStatus(userId, request);
        } catch (Exception e) {
            log.warn("Failed to block Appwrite user {}: {}", userId, e.getMessage());
        }
    }

    public SavedStatus<String> updatePref(String id, List<PrefRole> prefRoles) {
        appwriteClient.updatePrefs(id, prefRoles);
        return SavedStatus.build(ESaveStatus.SUCCESS, "success");
    }
}
