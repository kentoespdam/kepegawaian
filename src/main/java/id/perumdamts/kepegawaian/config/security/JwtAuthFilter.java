package id.perumdamts.kepegawaian.config.security;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.appwrite.Prefs;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtTokenService service;
    private static final String BEARER = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";
    @Value("${spring.profiles.active}")
    String profile;

    @SuppressWarnings("NullableProblems")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        UsernamePasswordAuthenticationToken authentication = profile.equals("development") ?
                getDevelopmentAuthentication() :
                getAuthentication(request);
        if (Objects.isNull(authentication)) {
            filterChain.doFilter(request, response);
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String tokenString = request.getHeader(AUTHORIZATION);
        if (Objects.isNull(tokenString) || !tokenString.startsWith(BEARER)) {
//            log.error("invalid header: {}", tokenString);
            return null;
        }
        String token = tokenString.substring(BEARER.length());
        AppwriteUser userFromToken = service.getUserFromToken(token);
        if (Objects.isNull(userFromToken)) {
//            log.error("invalid token: {}", token);
            return null;
        }
        return new UsernamePasswordAuthenticationToken(
                userFromToken,
                null,
                userFromToken.getAuthorities());
    }

    private UsernamePasswordAuthenticationToken getDevelopmentAuthentication() {
        List<String> roles = List.of("ADMIN");
        Prefs prefs = new Prefs();
        prefs.setRoles(roles);

        AppwriteUser userFromToken = new AppwriteUser();
        userFromToken.set$id("DEV");
        userFromToken.setName("DEVELOPMENT");
        userFromToken.setPrefs(prefs);

        return new UsernamePasswordAuthenticationToken(
                userFromToken,
                null,
                userFromToken.getAuthorities());
    }
}
