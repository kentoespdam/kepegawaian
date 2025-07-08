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
import java.util.Objects;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
//    private final RedisHelper redisHelper;
    private final JwtTokenService service;
    private static final String BEARER = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";
    @Value("${spring.profiles.active}")
    String profile;

    /**
     * Main entry point for JWT authentication.
     *
     * @param request  incoming HTTP request
     * @param response outgoing HTTP response
     * @param filterChain the filter chain to continue with
     * @throws ServletException if the filter operation fails
     * @throws IOException      if the filter operation fails
     */
    @SuppressWarnings("NullableProblems")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        UsernamePasswordAuthenticationToken authentication = profile.equals("development") ?
                getDevelopmentAuthentication() :
                getAuthentication(request);
        if (Objects.isNull(authentication)) {
            // if authentication failed, just continue with the request
            filterChain.doFilter(request, response);
            return;
        }

        // check if the request is a POST request and if the CSRF token is valid
        // if it is, set the response to 409 Conflict and return
//        if (request.getMethod().equals("POST")) {
//            String csrfToken = request.getHeader("X-CSRF-TOKEN");
//            if (redisHelper.validateToken(csrfToken)) {
//                response.setStatus(HttpServletResponse.SC_CONFLICT);
//                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//                response.getWriter().write("{\"message\": \"Duplicate request detected\"}");
//                return;
//            }
//        }

        // if everything is valid, set the authentication token and continue with the request
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

    /**
     * Retrieves the authentication from the request.
     *
     * @param request the incoming HTTP request containing the JWT token
     * @return a UsernamePasswordAuthenticationToken if the token is valid, otherwise null
     */
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        // Get the Authorization header from request
        String tokenString = request.getHeader(AUTHORIZATION);

        // Check if the token is missing or does not start with "Bearer "
        if (Objects.isNull(tokenString) || !tokenString.startsWith(BEARER)) {
            return null;
        }

        // Extract the token by removing "Bearer " prefix
        String token = tokenString.substring(BEARER.length());

        // Retrieve the user from the token
        AppwriteUser userFromToken = service.getUserFromToken(token);

        // Return null if user retrieval failed
        if (Objects.isNull(userFromToken)) {
            return null;
        }

        // Return an authentication token containing the user and their authorities
        return new UsernamePasswordAuthenticationToken(
                userFromToken,
                null,
                userFromToken.getAuthorities());
    }

    /**
     * Returns a special development-only authentication token when the app is running in development mode.
     * This token is not validated by the Appwrite service and is purely for convenience during development.
     * It is not intended to be used in production.
     *
     * @return a UsernamePasswordAuthenticationToken containing the development user and their authorities
     */
    private UsernamePasswordAuthenticationToken getDevelopmentAuthentication() {
        // Create a set of roles for the development user
        Set<String> roles = Set.of("ADMIN", "SYSTEM");

        // Create the development user
        Prefs prefs = new Prefs();
        prefs.setRoles(roles);

        AppwriteUser userFromToken = new AppwriteUser();
        userFromToken.set$id("DEV");
        userFromToken.setName("DEVELOPMENT");
        userFromToken.setPrefs(prefs);

        // Return an authentication token containing the user and their authorities
        return new UsernamePasswordAuthenticationToken(
                userFromToken,
                null,
                userFromToken.getAuthorities());
    }
}
