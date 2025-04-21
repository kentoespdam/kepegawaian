package id.perumdamts.kepegawaian.controllers.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    @Value("${appwrite.endpoint}")
    private String appwriteUrl;
    @Value("${appwrite.project_id}")
    private String appwriteProjectId;
    @Value("${appwrite.api_key}")
    private String appwriteApiKey;
    private final WebClient webClient;

    @GetMapping("/session")
    public ResponseEntity<?> index(@RequestHeader(value = "X-Appwrite-JWT") String token) {
        String result = webClient.get()
                .uri(appwriteUrl + "/account/jwt")
                .header("X-Appwrite-JWT", token)
                .header("X-Appwrite-Project", appwriteProjectId)
                .header("X-Appwrite-Key", appwriteApiKey)
                .exchangeToMono(response -> response.bodyToMono(String.class))
                .block();
        return ResponseEntity.ok(result);
    }
}
