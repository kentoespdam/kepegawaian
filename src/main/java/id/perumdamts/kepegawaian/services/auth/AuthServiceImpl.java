package id.perumdamts.kepegawaian.services.auth;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUserPostRequest;
import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import id.perumdamts.kepegawaian.dto.appwrite.Prefs;
import id.perumdamts.kepegawaian.dto.auth.AuthPostRequest;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    @Value("${appwrite.endpoint}")
    private String appwriteUrl;
    @Value("${appwrite.project_id}")
    private String appwriteProjectId;
    @Value("${appwrite.api_key}")
    private String appwriteApiKey;

    private final WebClient webClient;

    @Override
    public SavedStatus<?> createUser(AuthPostRequest request) {
        AppwriteUserPostRequest user = AppwriteUserPostRequest.builder()
                .userId(request.getId())
                .email(request.getNipam() + "@perumdamts.com")
                .password(request.getPassword())
                .name(request.getNama())
                .build();

        WebClient.ResponseSpec responseSpec = webClient.post()
                .uri(appwriteUrl + "/users")
                .header("X-Appwrite-Project", appwriteProjectId)
                .header("X-Appwrite-Key", appwriteApiKey)
                .bodyValue(user)
                .retrieve();

        String block = responseSpec
                .bodyToMono(String.class)
                .block();
        System.out.println(block);

        if (!request.getRoles().isEmpty())
            updatePref(request.getId(), request.getRoles());

        return SavedStatus.build(ESaveStatus.SUCCESS, "User berhasil disimpan");
    }

    @Override
    public void createUser(Pegawai pegawai) {
        AppwriteUserPostRequest user = AppwriteUserPostRequest.builder()
                .userId(pegawai.getId().toString())
                .email(pegawai.getNipam() + "@perumdamts.com")
                .password("tirtasatria")
                .name(pegawai.getBiodata().getNama())
                .build();

        webClient.post()
                .uri(appwriteUrl + "/users")
                .header("X-Appwrite-Project", appwriteProjectId)
                .header("X-Appwrite-Key", appwriteApiKey)
                .bodyValue(user)
                .retrieve();

        List<PrefRole> prefRoles = List.of(new PrefRole("ADMIN"), new PrefRole("USER"));
        updatePref(pegawai.getId().toString(), prefRoles);
    }

    @Override
    public SavedStatus<?> updatePref(String id, List<PrefRole> prefRoles) {
        Prefs prefs = new Prefs();
        prefs.setRoles(prefRoles.stream().map(PrefRole::getId).collect(Collectors.toSet()));
        Map<String, Object> wrapper = Map.of("prefs", prefs);

        try {
            webClient.patch()
                    .uri(appwriteUrl + "/users/" + id + "/prefs")
                    .header("X-Appwrite-Project", appwriteProjectId)
                    .header("X-Appwrite-Key", appwriteApiKey)
                    .bodyValue(wrapper)
                    .retrieve();
        } catch (Exception e) {
            log.info(e.getMessage());
        }


        return SavedStatus.build(ESaveStatus.SUCCESS, "User berhasil disimpan");
    }
}
