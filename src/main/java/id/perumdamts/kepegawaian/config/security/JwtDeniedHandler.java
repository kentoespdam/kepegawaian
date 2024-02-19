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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtDeniedHandler implements ServerAccessDeniedHandler {
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        String message = denied.getMessage();
        CustomErrorResponse unauthorized = CustomErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
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
                    response.setStatusCode(HttpStatus.FORBIDDEN);
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    return response.writeWith(Mono.just(buffer));
                }).doOnSuccess(empty -> DataBufferUtils.release(buffer));
    }
}
