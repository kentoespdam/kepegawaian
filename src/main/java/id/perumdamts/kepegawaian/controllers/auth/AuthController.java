package id.perumdamts.kepegawaian.controllers.auth;

import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.helpers.RedisHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Autentikasi — Auth")
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
    private final RestClient restClient;
    private final RedisHelper redisHelper;
    private final String jwtHeader = "X-Appwrite-JWT";

    @Operation(summary = "List data dengan paginasi")
    @GetMapping("/session")
    public ResponseEntity<String> index(@RequestHeader(value = jwtHeader) String token) {
        String result = restClient.get()
                .uri(appwriteUrl + "/account")
                .header("X-Appwrite-JWT", token)
                .header("X-Appwrite-Project", appwriteProjectId)
                .header("X-Appwrite-Key", appwriteApiKey)
                .retrieve()
                .body(String.class);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "csrf token")
    @GetMapping("/csrf-token")
    public ResponseEntity<SingleResult<String>> csrfToken() {
        return CustomResult.any(redisHelper.generateToken());
    }
}
