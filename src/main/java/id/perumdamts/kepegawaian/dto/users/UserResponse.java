package id.perumdamts.kepegawaian.dto.users;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.appwrite.Prefs;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String nipam;
    private String nama;
    private Prefs prefs;
    private Boolean isBlocked;
    private Boolean isVerified;

    public static UserResponse build(Pegawai pegawai, @Nullable AppwriteUser user) {
        UserResponse response = new UserResponse();
        response.setId(pegawai.getId());
        response.setNipam(pegawai.getNipam());
        response.setNama(pegawai.getBiodata().getNama());
        if (user!=null) {
            response.setPrefs(user.getPrefs());
            response.setIsVerified(user.getEmailVerification());
            response.setIsBlocked(!user.getStatus());
        }
        return response;
    }
}
