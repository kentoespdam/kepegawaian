package id.perumdamts.kepegawaian.config;

import id.perumdamts.kepegawaian.config.security.JwtAuthEntryPoint;
import id.perumdamts.kepegawaian.config.security.JwtAuthenticationManager;
import id.perumdamts.kepegawaian.config.security.JwtDeniedHandler;
import id.perumdamts.kepegawaian.config.security.JwtServerAuthenticationConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfig {
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JwtDeniedHandler  jwtDeniedHandler;
    private static final String[] WHITE_LIST = {"/api-docs/**", "/webjars/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**"};

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            JwtAuthenticationManager authenticationManager,
            JwtServerAuthenticationConverter jwtServerAuthenticationConverter
    ) {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(jwtServerAuthenticationConverter);

        return http
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthEntryPoint)
                        .accessDeniedHandler(jwtDeniedHandler)
                )
                .authorizeExchange(auth -> auth
                        .pathMatchers(WHITE_LIST).permitAll()
                        .anyExchange().authenticated())
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)

                .build();
    }
}
