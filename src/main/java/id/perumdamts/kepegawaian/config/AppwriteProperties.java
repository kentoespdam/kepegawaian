package id.perumdamts.kepegawaian.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "appwrite")
public class AppwriteProperties {
    private String endpoint;
    private String projectId;
    private String apiKey;
}
