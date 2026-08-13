package id.perumdamts.kepegawaian.controllers.system;

import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.system.roles.PrefRoleRequest;
import id.perumdamts.kepegawaian.dto.system.roles.PrefRoleStoreRequest;
import id.perumdamts.kepegawaian.dto.system.roles.PrefRoleUpdateRequest;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.PrefRoleRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/system/roles")
@RequiredArgsConstructor
public class PrefRoleController {
    // ADR-0039: SYSTEM (bootstrap guard endpoint /system/**) & ADMIN (fallback dual-mode hasRole)
    // tidak bisa dihapus — mencegah lockout/perubahan akses yang tidak disengaja.
    private static final Set<String> PROTECTED_ROLES = Set.of("SYSTEM", "ADMIN");

    private final PrefRoleRepository repository;

    @PreAuthorize("hasRole('SYSTEM') or hasAuthority('SYSTEM:MANAGE_ROLE')")
    @GetMapping
    public ResponseEntity<PageResult<Page<PrefRole>>> index(@Valid @ParameterObject PrefRoleRequest request) {
        Page<PrefRole> result = repository.findAll(request.getSpecification(), request.getPageable());
        return CustomResult.page(result);
    }

    @PreAuthorize("hasRole('SYSTEM') or hasAuthority('SYSTEM:MANAGE_ROLE')")
    @GetMapping("/list")
    public ResponseEntity<ListResult<PrefRole>> list() {
        List<PrefRole> all = repository.findAll();
        return CustomResult.list(all);
    }

    @PreAuthorize("hasRole('SYSTEM') or hasAuthority('SYSTEM:MANAGE_ROLE')")
    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<PrefRole>> show(@PathVariable String id) {
        PrefRole role = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role tidak ditemukan"));
        return CustomResult.any(role);
    }

    @PreAuthorize("hasRole('SYSTEM') or hasAuthority('SYSTEM:MANAGE_ROLE')")
    @PostMapping
    public ResponseEntity<SavedResult<String>> store(@Valid @RequestBody PrefRoleStoreRequest request) {
        boolean isExist = repository.existsById(request.getId());
        if (isExist) {
            throw new ConflictException("Role sudah ada");
        }
        repository.save(new PrefRole(request.getId(), request.getDescription()));
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, "success"));
    }

    @PreAuthorize("hasRole('SYSTEM') or hasAuthority('SYSTEM:MANAGE_ROLE')")
    @PutMapping("/{id}")
    public ResponseEntity<SavedResult<String>> update(@PathVariable String id,
                                                      @RequestBody PrefRoleUpdateRequest request) {
        PrefRole role = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role tidak ditemukan"));
        role.setDescription(request.getDescription());
        repository.save(role);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, "success"));
    }

    @PreAuthorize("hasRole('SYSTEM') or hasAuthority('SYSTEM:MANAGE_ROLE')")
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> destroy(@PathVariable String id) {
        if (PROTECTED_ROLES.contains(id)) {
            throw new ConflictException("Role " + id + " tidak bisa dihapus (bootstrap/proteksi)");
        }
        PrefRole role = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role tidak ditemukan"));
        // Hibernate menghapus baris pref_role_permission (ManyToMany) sebelum pref_role
        repository.delete(role);
        return CustomResult.delete(true);
    }
}
