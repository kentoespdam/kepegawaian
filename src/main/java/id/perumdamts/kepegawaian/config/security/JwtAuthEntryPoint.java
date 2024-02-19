package id.perumdamts.kepegawaian.config.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.perumdamts.kepegawaian.dto.commons.CustomErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthEntryPoint implements ServerAuthenticationEntryPoint {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        String message = ex.getMessage();
        CustomErrorResponse unauthorized = CustomErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(message)
                .path(exchange.getRequest().getPath().toString())
                .timestamp(System.currentTimeMillis())
                .build();
        byte[] bytes;
        try {
            bytes = mapper.writeValueAsBytes(unauthorized);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return Mono.just(message)
                .flatMap(s -> {
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    return response.writeWith(Mono.just(buffer));
                }).doOnSuccess(empty -> DataBufferUtils.release(buffer));
    }

}
