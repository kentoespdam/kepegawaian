package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class LaporanKepegawaianService {
    @Value("${laporan.kepegawaian.endpoint}")
    private String ENDPOINT;
    @Autowired
    private WebClient webClient;

    private ClientResponse.Headers headers;

    public ResponseEntity<String> getHtml(String path) {
        try {
            String block = webClient.get()
                    .uri(ENDPOINT + path)
                    .exchangeToMono(clientResponse -> {
                        this.headers = clientResponse.headers();
                        return clientResponse.bodyToMono(String.class);
                    }).block();
            return ResponseEntity.ok()
                    .headers(this.headers.asHttpHeaders())
                    .body(block);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    public Object getObject(String path) {
        try {
            return webClient.get()
                    .uri(ENDPOINT + path)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }

    public ResponseEntity<?> getExport(String path) {
        ExportResponse exportResponse = fetchExport(path);
        return ResponseEntity.ok()
                .headers(exportResponse.getHeaders().asHttpHeaders())
                .body(exportResponse.getResource());
    }

    // return ByteArrayResource and HttpHeaders from api
    private ExportResponse fetchExport(String path) {
        ExportResponse exportResponse = new ExportResponse();
        ByteArrayResource block = webClient.get()
                .uri(ENDPOINT + path)
                .exchangeToMono(clientResponse -> {
                    exportResponse.setHeaders(clientResponse.headers());
                    return clientResponse.bodyToMono(ByteArrayResource.class);

                }).block();
        exportResponse.setResource(block);
        return exportResponse;
    }

    @Data
    private static class ExportResponse {
        private ByteArrayResource resource;
        private ClientResponse.Headers headers;
    }
}