package id.perumdamts.kepegawaian.dto.appwrite;

import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
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