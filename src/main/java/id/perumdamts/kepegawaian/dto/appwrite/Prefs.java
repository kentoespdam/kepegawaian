package id.perumdamts.kepegawaian.dto.appwrite;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
// Appwrite prefs is a free-form object; other keys may exist alongside "roles" — ignore them.
@JsonIgnoreProperties(ignoreUnknown = true)
public class Prefs {
    private Set<String> roles;

    public List<String> getRoles() {
        if (roles == null)
            return List.of();
        return roles.stream()
                .map(String::toUpperCase)
                .toList();
    }
}