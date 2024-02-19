package id.perumdamts.kepegawaian.config.security;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenService {
    private final WebClient webClient;
    @Value("${appwrite.endpoint}")
    private String ENDPOINT;
    @Value("${appwrite.project_id}")
    private String PROJECT_ID;
    @Value("${appwrite.api_key}")
    private String API_KEY;

    public AppwriteUser getUserFromToken(String token) {
        try {
            return webClient.get()
                    .uri(ENDPOINT + "/account/jwt")
                    .header("X-Appwrite-Project", PROJECT_ID)
                    .header("X-Appwrite-Key", API_KEY)
                    .header("X-Appwrite-JWT", token)
                    .retrieve()
                    .bodyToMono(AppwriteUser.class)
                    .block();
        } catch (Exception e) {
            log.error("JWT Auth Error: {}", e.getMessage());
            return null;
        }
    }
}
