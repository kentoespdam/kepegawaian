package id.perumdamts.kepegawaian.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "appwrite")
public record AppwriteProperties(
        String endpoint,
        String projectId,
        String apiKey
) {}
