package id.perumdamts.kepegawaian.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class DownloadPenggajian {
    private final WebClient webClient;

    @Value("${penggajian.endpoint}")
    private String ENDPOINT;

    public Flux<ByteArrayResource> downloadTableGaji(String rootBatchId) {
        return webClient.get()
                .uri(ENDPOINT + "/export/table_gaji/"+rootBatchId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    throw new RuntimeException(
                            "Failed to download table gaji for root batch id " + rootBatchId + ": File Not Found!");
                })
                .bodyToFlux(ByteArrayResource.class);
    }

    public Flux<ByteArrayResource> downloadPotonganGaji(String rootBatchId) {
        return webClient.get()
                .uri(ENDPOINT + "/export/potongan/"+rootBatchId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    throw new RuntimeException(
                            "Failed to download template potongan gaji for root batch id " + rootBatchId + ": File Not Found!");
                })
                .bodyToFlux(ByteArrayResource.class);
    }
}
