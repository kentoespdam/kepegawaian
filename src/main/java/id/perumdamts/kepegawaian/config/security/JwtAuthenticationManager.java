package id.perumdamts.kepegawaian.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication)
                .cast(JwtToken.class)
                .filter(jwtToken -> {
//                    log.info("jwtToken: {}", jwtToken);
                    return jwtToken.getPrincipal() != null;
                })
                .map(JwtToken::withAuthenticated);
    }

    private <T> Mono<T> raiseBadCredentials() {
        log.info("Bad credentials");
        return Mono.error(new BadCredentialsException("Bad credentials"));
    }
}
