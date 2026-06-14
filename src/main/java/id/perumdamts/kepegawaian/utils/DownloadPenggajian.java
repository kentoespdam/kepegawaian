package id.perumdamts.kepegawaian.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class DownloadPenggajian {
    private final RestClient restClient;

    @Value("${penggajian.endpoint}")
    private String ENDPOINT;

    public ByteArrayResource downloadTableGaji(String rootBatchId) {
        return restClient.get()
                .uri(ENDPOINT + "/export/table_gaji/"+rootBatchId)
                .retrieve()
                .body(ByteArrayResource.class);
    }

    public ByteArrayResource downloadPotonganGaji(String rootBatchId) {
        return restClient.get()
                .uri(ENDPOINT + "/export/potongan/"+rootBatchId)
                .retrieve()
                .body(ByteArrayResource.class);
    }
}
