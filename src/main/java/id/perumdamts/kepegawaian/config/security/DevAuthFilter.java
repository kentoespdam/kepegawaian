package id.perumdamts.kepegawaian.config.security;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.appwrite.Prefs;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
public class DevAuthFilter extends OncePerRequestFilter {

    @Value("${security.dev.roles:ADMIN,SYSTEM}")
    private String devRoles;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            Set<String> roles = Set.of(devRoles.split(","));
            Prefs prefs = new Prefs();
            prefs.setRoles(roles);

                        AppwriteUser devUser = new AppwriteUser(
                    null, null, Boolean.FALSE, null, null,
                    null, Boolean.FALSE, Boolean.FALSE, prefs
            );
            devUser.set$id("DEV");

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(devUser, null, devUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
