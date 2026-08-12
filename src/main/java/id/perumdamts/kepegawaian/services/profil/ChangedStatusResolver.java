package id.perumdamts.kepegawaian.services.profil;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ChangedStatusResolver {

    /**
     * Returns true when the current principal does NOT hold ROLE_ADMIN nor ROLE_HRD,
     * meaning the change must enter the approval queue.
     * Returns false for ADMIN/HRD users (stable, no approval queue) — ADR-0036 §2.
     */
    public boolean requiresApproval() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AppwriteUser user)) {
            return true;
        }
        return user.getAuthorities().stream()
                .noneMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()) || "ROLE_HRD".equals(a.getAuthority()));
    }

    /**
     * Appwrite {@code $id} dari principal saat ini; null bila tidak ada sesi
     * (mis. seed sistem). Dipakai untuk stamping {@code disetujuiOleh}.
     */
    public String currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AppwriteUser user)) {
            return null;
        }
        return user.get$id();
    }
}
