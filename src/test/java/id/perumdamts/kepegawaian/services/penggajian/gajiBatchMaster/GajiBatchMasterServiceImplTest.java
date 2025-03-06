package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMaster;

import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchRoot;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchMasterRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchRootRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@SpringBootTest
@Slf4j
@ActiveProfiles("test")
class GajiBatchMasterServiceImplTest {
    @Autowired
    private WebClient webClient;
    @Getter
    private String contentType = "";
    @Getter
    private String fileName = "";
    @Autowired
    private GajiBatchMasterRepository repository;
    @Autowired
    private GajiBatchRootRepository gajiBatchRootRepository;

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

    @Test
    void peekGajiBatchMaster() {
        String rootBatchId = "202401-001";
        Long id = 10058L;
        Optional<GajiBatchRoot> gajiBatchRoot = gajiBatchRootRepository.findById(rootBatchId);
        if (gajiBatchRoot.isPresent()) {
            log.info("entity: {}", gajiBatchRoot.get());
        } else {
            log.info("not found");
        }
//        List<GajiBatchMaster> byGajiBatchRootBatchId = repository.findAll();
//                .findByGajiBatchRoot_Id("202401-001");
//        log.info("length: {}", byGajiBatchRootBatchId.size());
        Optional<GajiBatchMaster> byId = repository.findById(id);
        if (byId.isPresent()) {
            log.info("entity: {}", byId.get().getNipam());
        } else {
            log.info("not found");
        }
    }
}