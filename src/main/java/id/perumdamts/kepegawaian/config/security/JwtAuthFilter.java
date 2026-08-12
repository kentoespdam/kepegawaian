package id.perumdamts.kepegawaian.config.security;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.repositories.PrefRoleRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtTokenService service;
    private final PrefRoleRepository prefRoleRepository;
    private static final String BEARER = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";

    /**
     * Main entry point for JWT authentication.
     *
     * @param request     incoming HTTP request
     * @param response    outgoing HTTP response
     * @param filterChain the filter chain to continue with
     * @throws ServletException if the filter operation fails
     * @throws IOException      if the filter operation fails
     */
    @SuppressWarnings("NullableProblems")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
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
        String tokenString = request.getHeader(AUTHORIZATION);

        if (Objects.isNull(tokenString) || !tokenString.startsWith(BEARER)) {
            return null;
        }

        String token = tokenString.substring(BEARER.length());
        if (token.isBlank()) {
            return null;
        }
        AppwriteUser userFromToken = service.getUserFromToken(token);

        if (Objects.isNull(userFromToken)) {
            return null;
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>(userFromToken.getAuthorities());
        authorities.addAll(inflatePermissions(userFromToken.getPrefs().getRoles()));

        return new UsernamePasswordAuthenticationToken(
                userFromToken,
                null,
                authorities);
    }

    /**
     * Permission inflation (ADR-0037): load semua permission dari DB untuk roles
     * yang dimiliki user, lalu inject sebagai authority "ENTITY:ACTION" (tanpa prefix).
     * Union semantics — role yang tidak ada di DB di-skip.
     */
    List<SimpleGrantedAuthority> inflatePermissions(List<String> roles) {
        if (roles.isEmpty()) {
            return List.of();
        }
        return prefRoleRepository.findAllById(roles).stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .distinct()
                .toList();
    }
}
