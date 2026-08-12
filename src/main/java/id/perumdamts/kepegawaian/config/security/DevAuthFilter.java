package id.perumdamts.kepegawaian.config.security;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.appwrite.Prefs;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@Profile("development")
public class DevAuthFilter extends OncePerRequestFilter {

    private static final String BEARER = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";

    // Semua permission yang dikenal (katalog docs/plans/rbac-permission-per-role.md),
    // hardcoded supaya Dev User bisa test semua endpoint tanpa seed DB (ADR-0037).
    private static final List<String> DEV_PERMISSIONS = List.of(
            "MASTER:READ", "MASTER:WRITE", "MASTER:DELETE",
            "PEGAWAI:READ", "PEGAWAI:WRITE", "PEGAWAI:DELETE",
            "KEPEGAWAIAN:READ", "KEPEGAWAIAN:WRITE", "KEPEGAWAIAN:DELETE",
            "PROFIL:READ", "PROFIL:UPDATE", "PROFIL:APPROVE",
            "CUTI:READ", "CUTI:CREATE", "CUTI:APPROVE",
            "PENGGAJIAN:READ", "PENGGAJIAN:WRITE", "PENGGAJIAN:PROCESS",
            "SYSTEM:MANAGE_USER", "SYSTEM:MANAGE_ROLE");

    @Value("${security.dev.roles:ADMIN,SYSTEM}")
    private String devRoles;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Ada Bearer token: biarkan JwtAuthFilter yang memutuskan
        // (valid -> user asli, invalid -> 401 strict). Jangan inject, jangan clear.
        if (hasBearerToken(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Set<String> roles = Set.of(devRoles.split(","));
            Prefs prefs = new Prefs();
            prefs.setRoles(roles);

            AppwriteUser devUser = new AppwriteUser(
                    null, null, Boolean.FALSE, null, null,
                    null, Boolean.FALSE, Boolean.FALSE, prefs
            );
            devUser.set$id("DEV");

            List<SimpleGrantedAuthority> authorities = new ArrayList<>(devUser.getAuthorities());
            authorities.addAll(DEV_PERMISSIONS.stream().map(SimpleGrantedAuthority::new).toList());

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(devUser, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private boolean hasBearerToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION);
        return header != null
                && header.startsWith(BEARER)
                && !header.substring(BEARER.length()).isBlank();
    }
}
