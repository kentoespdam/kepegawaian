package id.perumdamts.kepegawaian.services.users;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.appwrite.Prefs;
import id.perumdamts.kepegawaian.dto.users.UserRequest;
import id.perumdamts.kepegawaian.dto.users.UserResponse;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.repositories.PegawaiRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
//@ActiveProfiles("test")
@Slf4j
class UserServiceImplTest {
    @Autowired
    private PegawaiRepository repository;
    private UserRequest request;
    @Autowired
    private WebClient webClient;
    @Value("${appwrite.endpoint}")
    private String appwriteUrl;
    @Value("${appwrite.project_id}")
    private String appwriteProjectId;
    @Value("${appwrite.api_key}")
    private String appwriteApiKey;

    @Autowired
    private Executor taskExecutor;

    @BeforeEach
    void setup() {
        this.request = new UserRequest();
//        request.setNipam("900800456");
//        request.setNama("BAGUS");
    }

    /**
     * This test will get the list of pegawai and then fetch the user info from appwrite
     * and then create a new {@link UserResponse} that contains pegawai and user info
     * and then return a new page of {@link UserResponse}
     */
    @Test
    void getPegawai() {
        // Get the list of pegawai
        Page<Pegawai> page = repository.findAll(request.getSpecification(), request.getPageable());
        List<Pegawai> pegawaiList = page.getContent();

        // Create a list of CompletableFuture of UserResponse
        List<CompletableFuture<UserResponse>> futures = pegawaiList.stream()
                .map(this::fetchUserAsync)
                .toList();

        // Wait for all the futures to complete and get the results
        List<UserResponse> results = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        // Check if the number of results is the same as the number of pegawai
        assertEquals(pegawaiList.size(), results.size());

        // Create a new page of UserResponse
        Page<UserResponse> pageResult = new PageImpl<>(results, page.getPageable(), page.getTotalElements());

        // Print the result
        log.info("result: {} ,{}, {}", pageResult.getTotalElements(), pageResult.getNumberOfElements(), pageResult.getContent());
    }

    @Test
    void fetUserTest() {
        fetchUser(483L);
    }

    private CompletableFuture<UserResponse> fetchUserAsync(Pegawai pegawai) {
        return CompletableFuture.supplyAsync(() -> {
            AppwriteUser appwriteUser = fetchUser(pegawai.getId());
            return UserResponse.build(pegawai, appwriteUser);
        }, taskExecutor);
    }

    @Async
    private AppwriteUser fetchUser(Long id) {
        return webClient.get()
                .uri(appwriteUrl + "/users/" + id)
                .header("Content-Type", "application/json")
                .header("X-Appwrite-Response-Format", "1.0.0")
                .header("X-Appwrite-Project", appwriteProjectId)
                .header("X-Appwrite-Key", appwriteApiKey)
                .exchangeToMono(response -> response.bodyToMono(AppwriteUser.class))
                .block();
    }

    @Test
    void getPref() {
        Prefs result = webClient.get()
                .uri(appwriteUrl + "/users/483/prefs")
                .header("Content-Type", "application/json")
                .header("X-Appwrite-Response-Format", "1.0.0")
                .header("X-Appwrite-Project", appwriteProjectId)
                .header("X-Appwrite-Key", appwriteApiKey)
                .exchangeToMono(response -> response.bodyToMono(Prefs.class))
                .block();
        log.info("result: {}", result);
    }

    @Test
    void queryTest(){
        String block = webClient.get()
                .uri(appwriteUrl + "/users?queries[]={\"method\":\"equal\",\"attribute\":\"status\",\"values\":[true]}")
                .header("Content-Type", "application/json")
                .header("X-Appwrite-Response-Format", "1.0.0")
                .header("X-Appwrite-Project", appwriteProjectId)
                .header("X-Appwrite-Key", appwriteApiKey)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(String.class))
                .block();
        log.info("block: {}", block);

    }
}