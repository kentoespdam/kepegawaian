package id.perumdamts.kepegawaian.controllers.system;

import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.entities.system.PrefPermission;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.PrefPermissionRepository;
import id.perumdamts.kepegawaian.repositories.PrefRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Sistem — Pref Permission")
@RestController
@RequestMapping("/system")
@RequiredArgsConstructor
public class PrefPermissionController {
    private final PrefRoleRepository roleRepository;
    private final PrefPermissionRepository permissionRepository;

    @PreAuthorize("hasRole('SYSTEM') or hasAuthority('SYSTEM:MANAGE_ROLE')")
    @Operation(summary = "Daftar semua data")
    @GetMapping("/permissions")
    public ResponseEntity<ListResult<PrefPermission>> list() {
        return CustomResult.list(permissionRepository.findAll());
    }

    @PreAuthorize("hasRole('SYSTEM') or hasAuthority('SYSTEM:MANAGE_ROLE')")
    @Transactional
    @Operation(summary = "assign")
    @PostMapping("/roles/{roleId}/permissions/{permName}")
    public ResponseEntity<SavedResult<String>> assign(@PathVariable String roleId, @PathVariable String permName) {
        PrefRole role = roleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Role tidak ditemukan"));
        PrefPermission permission = permissionRepository.findById(permName)
                .orElseThrow(() -> new NotFoundException("Permission tidak ditemukan"));
        if (!role.getPermissions().add(permission)) {
            throw new ConflictException("Permission sudah ter-assign ke role");
        }
        roleRepository.save(role);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, "success"));
    }

    @PreAuthorize("hasRole('SYSTEM') or hasAuthority('SYSTEM:MANAGE_ROLE')")
    @Transactional
    @Operation(summary = "revoke")
    @DeleteMapping("/roles/{roleId}/permissions/{permName}")
    public ResponseEntity<DeletedResult> revoke(@PathVariable String roleId, @PathVariable String permName) {
        PrefRole role = roleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Role tidak ditemukan"));
        PrefPermission permission = permissionRepository.findById(permName)
                .orElseThrow(() -> new NotFoundException("Permission tidak ditemukan"));
        if (!role.getPermissions().remove(permission)) {
            throw new NotFoundException("Permission tidak ter-assign ke role");
        }
        roleRepository.save(role);
        return CustomResult.delete(true);
    }
}
