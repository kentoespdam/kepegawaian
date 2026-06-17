package id.perumdamts.kepegawaian.config.audit;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditAwareImpl implements AuditorAware<String> {

    @SuppressWarnings("NullableProblems")
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AppwriteUser user)) {
            return Optional.of("system");
        }
        return Optional.of(user.get$id());
    }
}
