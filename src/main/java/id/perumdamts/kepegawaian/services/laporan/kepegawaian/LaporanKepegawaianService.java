package id.perumdamts.kepegawaian.services.laporan.kepegawaian;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class LaporanKepegawaianService {
    @Value("${laporan.kepegawaian.endpoint}")
    private String ENDPOINT;
    @Autowired
    private RestClient restClient;

    public ResponseEntity<String> getHtml(String path) {
        try {
            String body = restClient.get()
                    .uri(ENDPOINT + path)
                    .retrieve()
                    .body(String.class);
            return ResponseEntity.ok()
                    .body(body);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    public Object getObject(String path) {
        try {
            return restClient.get()
                    .uri(ENDPOINT + path)
                    .retrieve()
                    .body(Object.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public ResponseEntity<?> getExport(String path) {
        ExportResponse exportResponse = fetchExport(path);
        return ResponseEntity.ok()
                .headers(exportResponse.getHeaders())
                .body(exportResponse.getResource());
    }

    // return ByteArrayResource and HttpHeaders from api
    private ExportResponse fetchExport(String path) {
        ExportResponse exportResponse = new ExportResponse();
        ByteArrayResource resource = restClient.get()
                .uri(ENDPOINT + path)
                .retrieve()
                .body(ByteArrayResource.class);
        exportResponse.setResource(resource);
        exportResponse.setHeaders(new HttpHeaders());
        return exportResponse;
    }

    @Data
    private static class ExportResponse {
        private ByteArrayResource resource;
        private HttpHeaders headers;
    }
}