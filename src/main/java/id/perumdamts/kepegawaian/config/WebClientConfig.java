package id.perumdamts.kepegawaian.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Component
public class WebClientConfig {
    // RestClient.builder() is used deliberately: Boot 4.0 modularized web auto-configuration out of
    // spring-boot-autoconfigure, so beans like RestClient.Builder / HttpMessageConverters are NOT
    // available in this context. The default (strict) mapper is fine because AppwriteUser and Prefs
    // tolerate unknown fields via @JsonIgnoreProperties(ignoreUnknown = true).
    //
    // JdkClientHttpRequestFactory is pinned to HTTP/1.1: the JDK HttpClient default (HTTP/2) sends
    // an h2c preface on cleartext http:// URLs, which HTTP/1.1-only proxies (e.g. the Appwrite
    // nginx at :82) never answer — every call hangs. connectTimeout guards against silent hangs
    // when the endpoint is unreachable.
    @Bean
    public RestClient restClient() {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(client);
        factory.setReadTimeout(Duration.ofSeconds(10));
        return RestClient.builder()
                .requestFactory(factory)
                .build();
    }
}
