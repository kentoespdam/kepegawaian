package id.perumdamts.kepegawaian.controllers.system;

import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.enums.AppPermission;
import id.perumdamts.kepegawaian.services.system.PrefPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Sistem — Pref Permission")
@RestController
@RequestMapping("/system")
@RequiredArgsConstructor
public class PrefPermissionController {
    private final PrefPermissionService permissionService;

    @PreAuthorize("hasRole('SYSTEM') or hasAuthority('" + AppPermission.Authority.SYSTEM_MANAGE_ROLE + "')")
    @Operation(summary = "Daftar semua data")
    @GetMapping("/permissions")
    public ResponseEntity<ListResult<AppPermission>> list() {
        return CustomResult.list(List.of(AppPermission.values()));
    }

    @PreAuthorize("hasRole('SYSTEM') or hasAuthority('" + AppPermission.Authority.SYSTEM_MANAGE_ROLE + "')")
    @Operation(summary = "assign")
    @PostMapping("/roles/{roleId}/permissions/{permName}")
    public ResponseEntity<SavedResult<String>> assign(@PathVariable String roleId, @PathVariable String permName) {
        permissionService.assign(roleId, permName);
        return CustomResult.save(SavedStatus.build(ESaveStatus.SUCCESS, "success"));
    }

    @PreAuthorize("hasRole('SYSTEM') or hasAuthority('" + AppPermission.Authority.SYSTEM_MANAGE_ROLE + "')")
    @Operation(summary = "revoke")
    @DeleteMapping("/roles/{roleId}/permissions/{permName}")
    public ResponseEntity<DeletedResult> revoke(@PathVariable String roleId, @PathVariable String permName) {
        permissionService.revoke(roleId, permName);
        return CustomResult.delete(true);
    }
}
