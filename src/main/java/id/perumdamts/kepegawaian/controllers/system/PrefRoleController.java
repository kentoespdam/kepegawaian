package id.perumdamts.kepegawaian.controllers.system;

import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import id.perumdamts.kepegawaian.dto.commons.*;
import id.perumdamts.kepegawaian.dto.system.roles.PrefRoleRequest;
import id.perumdamts.kepegawaian.dto.system.roles.PrefRoleStoreRequest;
import id.perumdamts.kepegawaian.dto.system.roles.PrefRoleUpdateRequest;
import id.perumdamts.kepegawaian.exceptions.ConflictException;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import id.perumdamts.kepegawaian.repositories.PrefRoleRepository;
import id.perumdamts.kepegawaian.services.system.PrefRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Sistem — Pref Role")
@RestController
@RequestMapping("/system/roles")
@RequiredArgsConstructor
public class PrefRoleController {
    private final PrefRoleRepository repository;
    private final PrefRoleService roleService;

    @PreAuthorize("hasRole('SYSTEM') or hasAuthority('SYSTEM:MANAGE_ROLE')")
    @Operation(summary = "List data dengan paginasi")
    @GetMapping
    public ResponseEntity<PageResult<Page<PrefRole>>> index(@Valid @ParameterObject PrefRoleRequest request) {
        Page<PrefRole> result = repository.findAll(request.getSpecification(), request.getPageable());
        return CustomResult.page(result);
    }

    @PreAuthorize("hasRole('SYSTEM') or hasAuthority('SYSTEM:MANAGE_ROLE')")
    @Operation(summary = "Daftar semua data")
    @GetMapping("/list")
    public ResponseEntity<ListResult<PrefRole>> list() {
        return CustomResult.list(repository.findAll());
    }

    @PreAuthorize("hasRole('SYSTEM') or hasAuthority('SYSTEM:MANAGE_ROLE')")
    @Operation(summary = "show")
    @GetMapping("/{id}")
    public ResponseEntity<SingleResult<PrefRole>> show(@PathVariable String id) {
        PrefRole role = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role tidak ditemukan"));
        return CustomResult.any(role);
    }

    @PreAuthorize("hasRole('SYSTEM') or hasAuthority('SYSTEM:MANAGE_ROLE')")
    @Operation(summary = "store")
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
    @Operation(summary = "Perbarui data")
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
    @Operation(summary = "destroy")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeletedResult> destroy(@PathVariable String id) {
        roleService.destroy(id);
        return CustomResult.delete(true);
    }
}
