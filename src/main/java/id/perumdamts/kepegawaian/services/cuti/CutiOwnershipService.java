package id.perumdamts.kepegawaian.services.cuti;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.exceptions.ForbiddenException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Ownership untuk self-service cuti (ADR-0038 pattern: identitas di-resolve server dari
 * principal, bukan dari body request — mencegah impersonasi pegawai lain).
 *
 * - ADMIN/HRD (ROLE_ADMIN / ROLE_HRD): boleh bertindak atas nama pegawai lain (kelola).
 * - DEV (profile development): bypass — tidak punya pegawai riil.
 * - Selain itu: wajib atas nama sendiri; mencoba pegawai lain → 403 Forbidden.
 */
@Service
@RequiredArgsConstructor
public class CutiOwnershipService {
    private final PegawaiRepository pegawaiRepository;

    /**
     * Resolve pegawai pemohon dari principal. Untuk non-privileged, {@code requestedPegawaiId}
     * wajib sama dengan id pegawai principal — jika tidak, 403.
     */
    public Pegawai resolvePemohon(Long requestedPegawaiId) {
        if (isPrivileged()) {
            return pegawaiRepository.findById(requestedPegawaiId)
                    .orElseThrow(() -> new NotFoundException("Unknown Pegawai"));
        }
        Long ownId = currentPegawaiId();
        if (!ownId.equals(requestedPegawaiId)) {
            throw new ForbiddenException("Tidak boleh bertindak atas nama pegawai lain");
        }
        return pegawaiRepository.findById(ownId)
                .orElseThrow(() -> new NotFoundException("Unknown Pegawai"));
    }

    /** Wajib: cuti/klaim milik pegawai principal sendiri, kecuali ADMIN/HRD/DEV. */
    public void assertOwns(Long pemilikPegawaiId) {
        if (!isPrivileged() && !currentPegawaiId().equals(pemilikPegawaiId)) {
            throw new ForbiddenException("Data milik pegawai lain");
        }
    }

    public boolean isPrivileged() {
        AppwriteUser user = currentUser();
        if (user == null) {
            return false;
        }
        Set<String> authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        return authorities.contains("ROLE_ADMIN")
                || authorities.contains("ROLE_HRD")
                || "DEV".equals(user.get$id());
    }

    public Long currentPegawaiId() {
        AppwriteUser user = currentUser();
        if (user == null || user.get$id() == null) {
            throw new ForbiddenException("Identitas tidak dikenal");
        }
        try {
            return Long.valueOf(user.get$id());
        } catch (NumberFormatException e) {
            throw new ForbiddenException("Identitas tidak valid");
        }
    }

    private AppwriteUser currentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AppwriteUser user) {
            return user;
        }
        return null;
    }
}
