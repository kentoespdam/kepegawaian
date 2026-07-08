package id.perumdamts.kepegawaian.config.appwrite;

import id.perumdamts.kepegawaian.config.AppwriteProperties;
import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUserPostRequest;
import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import id.perumdamts.kepegawaian.dto.appwrite.Prefs;
import id.perumdamts.kepegawaian.dto.users.UserPatchStatusRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppwriteClient {
    private final RestClient restClient;
    private final AppwriteProperties properties;

    public AppwriteUser getUser(String id) {
        return restClient.get()
                .uri(properties.getEndpoint() + "/users/" + id)
                .headers(headers -> addDefaultHeaders(headers, properties))
                .retrieve()
                .body(AppwriteUser.class);
    }

    public String createUser(AppwriteUserPostRequest request) {
        String response = restClient.post()
                .uri(properties.getEndpoint() + "/users")
                .headers(headers -> addDefaultHeaders(headers, properties))
                .body(request)
                .retrieve()
                .body(String.class);
        log.debug("Appwrite createUser response: {}", response);
        return response;
    }

    public AppwriteUser updateStatus(String id, UserPatchStatusRequest status) {
        return restClient.patch()
                .uri(properties.getEndpoint() + "/users/" + id + "/status")
                .headers(headers -> addDefaultHeaders(headers, properties))
                .body(status)
                .retrieve()
                .body(AppwriteUser.class);
    }

    public void updatePrefs(String id, List<PrefRole> prefRoles) {
        Prefs prefs = new Prefs();
        prefs.setRoles(prefRoles.stream().map(PrefRole::getId).collect(Collectors.toSet()));
        Map<String, Object> wrapper = Map.of("prefs", prefs);

        try {
            restClient.patch()
                    .uri(properties.getEndpoint() + "/users/" + id + "/prefs")
                    .headers(headers -> addDefaultHeaders(headers, properties))
                    .body(wrapper)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            log.warn("Failed to update Appwrite prefs for user {}: {}", id, e.getMessage());
        }
    }

    public void createUserWithDefaultRoles(String userId, String email, String password, String name) {
        AppwriteUserPostRequest user = AppwriteUserPostRequest.builder()
                .userId(userId)
                .email(email)
                .password(password)
                .name(name)
                .build();

        createUser(user);

        List<PrefRole> defaultRoles = List.of(new PrefRole("ADMIN"), new PrefRole("USER"));
        updatePrefs(userId, defaultRoles);
    }

    public AppwriteUser validateToken(String token) {
        try {
            return restClient.get()
                    .uri(properties.getEndpoint() + "/account/jwt")
                    .headers(headers -> {
                        addDefaultHeaders(headers, properties);
                        headers.set("X-Appwrite-JWT", token);
                    })
                    .retrieve()
                    .body(AppwriteUser.class);
        } catch (Exception e) {
            log.error("JWT Auth Error: {}", e.getMessage());
            return null;
        }
    }

    private static void addDefaultHeaders(org.springframework.http.HttpHeaders headers, AppwriteProperties props) {
        headers.set("Content-Type", "application/json");
        headers.set("X-Appwrite-Response-Format", "1.0.0");
        headers.set("X-Appwrite-Project", props.getProjectId());
        headers.set("X-Appwrite-Key", props.getApiKey());
    }
}
