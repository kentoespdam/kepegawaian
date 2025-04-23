package id.perumdamts.kepegawaian.dto.auth;

import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AuthPostRequest {
    private String id;
    private String nipam;
    private String nama;
    private String password;
    private List<PrefRole> roles;

    public String getPassword() {
        return password == null ? "tirtasatria" : password;
    }
}
