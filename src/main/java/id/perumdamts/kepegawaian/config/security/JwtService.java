package id.perumdamts.kepegawaian.config.security;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {
    private final WebClient webClient;
    @Value("${appwrite.endpoint}")
    private String ENDPOINT;
    @Value("${appwrite.project_id}")
    private String PROJECT_ID;
    @Value("${appwrite.api_key}")
    private String API_KEY;

    public Mono<AppwriteUser> getUser(String jwt) {
        return webClient.get()
                .uri(ENDPOINT + "/account/jwt")
                .header("Content-Type", "Application/json")
                .header("X-Appwrite-Project", PROJECT_ID)
                .header("X-Appwrite-Key", API_KEY)
                .header("X-Appwrite-JWT", jwt)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(String.class)
                        .map(RuntimeException::new))
                .bodyToMono(AppwriteUser.class);
    }
}
