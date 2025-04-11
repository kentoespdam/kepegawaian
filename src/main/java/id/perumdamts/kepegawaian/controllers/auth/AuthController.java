package id.perumdamts.kepegawaian.controllers.auth;

import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import id.perumdamts.kepegawaian.dto.auth.AuthPostRequest;
import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.services.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

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
    private final AuthService authService;

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

    @PostMapping()
    public ResponseEntity<?> create(@Valid @RequestBody AuthPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(authService.createUser(request));
    }

    @PatchMapping("/pref/{id}")
    public ResponseEntity<?> updatePref(@PathVariable String id, @RequestBody List<PrefRole> request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(authService.updatePref(id, request));
    }
}
