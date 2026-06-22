package id.perumdamts.kepegawaian.config;

import id.perumdamts.kepegawaian.config.security.DeniedHandler;
import id.perumdamts.kepegawaian.config.security.DevAuthFilter;
import id.perumdamts.kepegawaian.config.security.JwtAuthEntryPoint;
import id.perumdamts.kepegawaian.config.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurity {
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final DeniedHandler deniedHandler;
    private final JwtAuthFilter jwtAuthFilter;
    private final DevAuthFilter devAuthFilter;

    @Value("${custom.cors.allowed-origins:http://localhost:5173}")
    private String allowedOrigins;

    @Bean
    @Profile("!development")
    public SecurityFilterChain jwtFilterChain(HttpSecurity http) {
        try {
            return http
                    .cors(c -> c.configurationSource(corsConfigurationSource()))
                    .csrf(AbstractHttpConfigurer::disable)
                    .formLogin(AbstractHttpConfigurer::disable)
                    .httpBasic(AbstractHttpConfigurer::disable)
                    .exceptionHandling(e -> e.authenticationEntryPoint(jwtAuthEntryPoint)
                            .accessDeniedHandler(deniedHandler))
                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(authorization -> authorization
                            .requestMatchers("/api-docs/**").permitAll()
                            .requestMatchers("/swagger-ui.html").permitAll()
                            .requestMatchers("/swagger-ui/**").permitAll()
                            .requestMatchers("/v3/api-docs/**").permitAll()
                            .requestMatchers("/auth/**").permitAll()
                            .anyRequest().authenticated()
                    )
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    @Profile("development")
    public SecurityFilterChain devFilterChain(HttpSecurity http) {
        try {
            return http
                    .cors(c -> c.configurationSource(corsConfigurationSource()))
                    .csrf(AbstractHttpConfigurer::disable)
                    .formLogin(AbstractHttpConfigurer::disable)
                    .httpBasic(AbstractHttpConfigurer::disable)
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(authorization -> authorization
                            .anyRequest().permitAll()
                    )
                    .addFilterBefore(devAuthFilter, UsernamePasswordAuthenticationFilter.class)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigins.split(",")));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
