package id.perumdamts.kepegawaian.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class WebClientConfig {
    // RestClient.create() is used deliberately: Boot 4.0 modularized web auto-configuration out of
    // spring-boot-autoconfigure, so beans like RestClient.Builder / HttpMessageConverters are NOT
    // available in this context. The default (strict) mapper is fine because AppwriteUser and Prefs
    // tolerate unknown fields via @JsonIgnoreProperties(ignoreUnknown = true).
    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }
}
