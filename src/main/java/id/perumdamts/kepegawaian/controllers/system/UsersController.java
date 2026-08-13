package id.perumdamts.kepegawaian.controllers.system;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import id.perumdamts.kepegawaian.dto.auth.AuthPostRequest;
import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.SavedResult;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.dto.users.UserPatchStatusRequest;
import id.perumdamts.kepegawaian.dto.users.UserRequest;
import id.perumdamts.kepegawaian.dto.users.UserResponse;
import id.perumdamts.kepegawaian.services.auth.AuthService;
import id.perumdamts.kepegawaian.services.users.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/system/users")
public class UsersController {
    private final UserService service;
    private final AuthService authService;

    @PreAuthorize("hasRole('SYSTEM') or hasAuthority('SYSTEM:MANAGE_USER')")
    @GetMapping
    public ResponseEntity<SingleResult<Page<UserResponse>>> index(@Valid @ParameterObject UserRequest request) {
        return CustomResult.any(service.findPage(request));
    }

    @PreAuthorize("hasRole('SYSTEM') or hasAuthority('SYSTEM:MANAGE_USER')")
    @PostMapping()
    public ResponseEntity<SavedResult<String>> create(@Valid @RequestBody AuthPostRequest request) {
        return CustomResult.save(authService.createUser(request));
    }

    @PreAuthorize("hasRole('SYSTEM') or hasAuthority('SYSTEM:MANAGE_USER')")
    @PatchMapping("/pref/{id}")
    public ResponseEntity<SavedResult<String>> updatePref(@PathVariable String id, @RequestBody List<PrefRole> request) {
        return CustomResult.save(authService.updatePref(id, request));
    }

    @PreAuthorize("hasRole('SYSTEM') or hasAuthority('SYSTEM:MANAGE_USER')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<SavedResult<AppwriteUser>> patchStatus(@PathVariable String id, @Valid @RequestBody UserPatchStatusRequest request) {
        return CustomResult.save(service.patchStatus(id, request));
    }
}
