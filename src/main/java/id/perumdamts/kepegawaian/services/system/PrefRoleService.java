package id.perumdamts.kepegawaian.services.system;

import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.PrefRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PrefRoleService {
    // ADR-0039: SYSTEM (bootstrap guard endpoint /system/**) & ADMIN (fallback dual-mode hasRole)
    // tidak bisa dihapus — mencegah lockout/perubahan akses yang tidak disengaja.
    private static final Set<String> PROTECTED_ROLES = Set.of("SYSTEM", "ADMIN");

    private final PrefRoleRepository repository;

    @Transactional
    public void destroy(String id) {
        if (PROTECTED_ROLES.contains(id)) {
            throw new ConflictException("Role " + id + " tidak bisa dihapus (bootstrap/proteksi)");
        }
        PrefRole role = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role tidak ditemukan"));
        // Hibernate menghapus baris pref_role_permission (ManyToMany) sebelum pref_role
        repository.delete(role);
    }
}
