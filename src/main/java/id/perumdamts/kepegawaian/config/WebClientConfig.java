package id.perumdamts.kepegawaian.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class WebClientConfig {
    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }
}
