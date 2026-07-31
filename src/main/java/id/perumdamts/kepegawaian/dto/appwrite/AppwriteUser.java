package id.perumdamts.kepegawaian.dto.appwrite;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
@Data
// Real Appwrite responses contain fields absent from this DTO (labels, targets, accessedAt, mfa,
// memberships, ...) — ignore them instead of failing deserialization under the strict mapper.
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppwriteUser {
    // Explicit $ field names: Jackson mangles getters like get$id() inconsistently across
    // versions (Jackson 2 maps it, Jackson 3 does not). @JsonProperty pins the JSON key on
    // the field AND the generated accessors so both Jackson 2 and 3 map the same property.
    @JsonProperty("$id")
    @Setter
    @Getter(onMethod_ = @JsonProperty("$id"))
    private String $id;
    @JsonProperty("$createdAt")
    @Getter(onMethod_ = @JsonProperty("$createdAt"))
    private String $createdAt;
    @JsonProperty("$updatedAt")
    @Getter(onMethod_ = @JsonProperty("$updatedAt"))
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
        return prefs.getRoles().stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)).toList();
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