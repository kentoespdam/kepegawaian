package id.perumdamts.kepegawaian.dto.appwrite;

import lombok.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AppwriteUser {
    @Setter
    @Getter
    private String $id;
    @Getter
    private String $createdAt;
    @Getter
    private String $updatedAt;
    private String name;
    private String registration;
    private Boolean status;
    private String passwordUpdate;
    private String email;
    private String phone;
    private Boolean emailVerification;
    private Boolean phoneVerification;
    private Prefs prefs;

    public Collection<SimpleGrantedAuthority> getAuthorities() {
        return prefs.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return "AppwriteUser{" +
                "$id='" + get$id() + '\'' +
                ", $createdAt='" + get$createdAt() + '\'' +
                ", $updatedAt='" + get$updatedAt() + '\'' +
                ", name='" + getName() + '\'' +
                ", registration='" + getRegistration() + '\'' +
                ", status=" + getStatus() +
                ", passwordUpdate='" + getPasswordUpdate() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", emailVerification=" + getEmailVerification() +
                ", phoneVerification=" + getPhone() +
                ", prefs=" + getPrefs() +
                '}';
    }
}