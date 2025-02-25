package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMaster;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest
@Slf4j
class GajiBatchMasterServiceImplTest {
    @Autowired
    private WebClient webClient;
    @Getter
    private String contentType = "";
    @Getter
    private String fileName = "";

//    @Test
    void downloadTableGaji() {
        String endpoint = "http://192.168.1.214:81";
        String rootBatchId = "202401";

        ByteArrayResource resource = webClient.get()
                .uri(endpoint + "/export/table_gaji/" + rootBatchId)
                .retrieve()
                .bodyToMono(ByteArrayResource.class)
                .block();

        assert resource != null;
        log.info("resource: {}", resource.contentLength());
    }
}