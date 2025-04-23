package id.perumdamts.kepegawaian.services.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUserPostRequest;
import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import id.perumdamts.kepegawaian.dto.appwrite.Prefs;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class AuthServiceImplTest {
    @Value("${appwrite.endpoint}")
    private String appwriteUrl;
    @Value("${appwrite.project_id}")
    private String appwriteProjectId;
    @Value("${appwrite.api_key}")
    private String appwriteApiKey;
    @Autowired
    private WebClient webClient;

    @Test
    void createUser() {
        log.info("{} || {} || {}", appwriteUrl, appwriteProjectId, appwriteApiKey);
        Mono<AppwriteUserPostRequest> user = Mono.just(AppwriteUserPostRequest.builder()
                .userId("jajal")
                .email("jajal@perumdamts.com")
                .phone("+62123456789012")
                .password("tirtasatria")
                .name("Jajal")
                .build());

        WebClient.ResponseSpec responseSpec = webClient.post()
                .uri(appwriteUrl + "/users")
//                .uri("http://localhost:1234/jajal.php")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Appwrite-Project", appwriteProjectId)
                .header("X-Appwrite-Key", appwriteApiKey)
                .body(user, AppwriteUserPostRequest.class)
                .retrieve();

        String block = responseSpec
                .bodyToMono(String.class)
                .block();
        log.info("block: {}", block);
    }

    @Test
    void testPref() {
        List<PrefRole> prefRoles = List.of(new PrefRole("ADMIN"), new PrefRole("USER"));
        Prefs prefs = new Prefs();
        prefs.setRoles(prefRoles.stream().map(PrefRole::getId).collect(Collectors.toSet()));

        Map<String, Object> wrapper=new HashMap<>();
        wrapper.put("prefs", prefs);

        log.info("Object: {}", prefs);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(wrapper);
            log.info("jsonString: {}", json);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }


}