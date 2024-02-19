package id.perumdamts.kepegawaian.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtServerAuthenticationConverter implements ServerAuthenticationConverter {
    private final JwtService service;
    private static final String BEARER = "Bearer ";

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        try {
            return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("Authorization"))
                    .filter(auth -> auth.startsWith(BEARER))
                    .map(auth -> auth.substring(BEARER.length()))
                    .flatMap(token -> createUserDetails(token)
                                    .map(userDetails -> new JwtToken(token, userDetails))
//                            .onErrorMap(throwable -> new RuntimeException(throwable.getMessage()))
                                    .onErrorResume(throwable -> Mono.error(new JwtAuthenticationException(throwable.getMessage())))
                                    .filter(jwtToken -> jwtToken.getPrincipal() != null)
                                    .switchIfEmpty(Mono.error(new Throwable("Invalid Token")))
                    );
        } catch (Exception e) {
            log.error("convert error: {}", e.getMessage());
            return Mono.error(new JwtAuthenticationException(e.getMessage()));
        }
    }

    private Mono<UserDetails> createUserDetails(String token) {
        Mono<UserDetails> map = service.getUser(token)
                .map(appwriteUser -> User
                        .builder()
                        .username(appwriteUser.get$id())
                        .password(token)
                        .authorities(appwriteUser.getAuthorities())
                        .build());
        map.map(userDetails -> {
            log.info("username: {}",userDetails.getUsername());
            return userDetails.getUsername();
        });
        return map;
    }
}
