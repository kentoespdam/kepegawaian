package id.perumdamts.kepegawaian.dto.appwrite;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
public class Prefs {
    private List<String> roles;

    public List<String> getRoles() {
        return roles.stream().map(r -> "ROLE_" + r).toList();
    }
}