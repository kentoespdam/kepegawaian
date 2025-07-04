package id.perumdamts.kepegawaian.config;

import id.perumdamts.kepegawaian.config.security.DeniedHandler;
import id.perumdamts.kepegawaian.config.security.JwtAuthEntryPoint;
import id.perumdamts.kepegawaian.config.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurity {
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final DeniedHandler deniedHandler;
    private final JwtAuthFilter jwtAuthFilter;

    /**
     * Security filter chain bean.
     *
     * @param http http security builder
     * @return security filter chain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        try {
            // Disable CORS
            // Disable CSRF
            // Disable form login
            // Disable HTTP basic auth
            // Configure exception handling
            // Add JWT auth filter
            // Set session creation policy to STATELESS
            // Authorize HTTP requests
            return http
                    .cors(AbstractHttpConfigurer::disable)
                    .csrf(AbstractHttpConfigurer::disable)
                    .formLogin(AbstractHttpConfigurer::disable)
                    .httpBasic(AbstractHttpConfigurer::disable)
                    .exceptionHandling(e -> e.authenticationEntryPoint(jwtAuthEntryPoint)
                            .accessDeniedHandler(deniedHandler))
                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(authorization -> authorization
                            // Allow API docs
                            .requestMatchers("/api-docs/**").permitAll()
                            .requestMatchers("/swagger-ui.html").permitAll()
                            .requestMatchers("/swagger-ui/**").permitAll()
                            .requestMatchers("/v3/api-docs/**").permitAll()
                            // Allow auth endpoints
                            .requestMatchers("/auth/**").permitAll()
                            // All other requests require authentication
                            .anyRequest().authenticated()
                    )
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
