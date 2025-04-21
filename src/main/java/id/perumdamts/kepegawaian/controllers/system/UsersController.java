package id.perumdamts.kepegawaian.controllers.system;

import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import id.perumdamts.kepegawaian.dto.auth.AuthPostRequest;
import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.ErrorResult;
import id.perumdamts.kepegawaian.dto.users.UserPatchStatusRequest;
import id.perumdamts.kepegawaian.dto.users.UserRequest;
import id.perumdamts.kepegawaian.services.auth.AuthService;
import id.perumdamts.kepegawaian.services.users.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/system/users")
public class UsersController {
    private final UserService service;
    private final AuthService authService;

    @PreAuthorize("hasRole('SYSTEM')")
    @GetMapping
    public ResponseEntity<?> index(@ParameterObject UserRequest request) {
        return CustomResult.any(service.findPage(request));
    }

    @PreAuthorize("hasRole('SYSTEM')")
    @PostMapping()
    public ResponseEntity<?> create(@Valid @RequestBody AuthPostRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(authService.createUser(request));
    }

    @PreAuthorize("hasRole('SYSTEM')")
    @PatchMapping("/pref/{id}")
    public ResponseEntity<?> updatePref(@PathVariable String id, @RequestBody List<PrefRole> request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(authService.updatePref(id, request));
    }

    @PreAuthorize("hasRole('SYSTEM')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> patchStatus(@PathVariable Long id, @Valid @RequestBody UserPatchStatusRequest request, Errors errors) {
        if (errors.hasErrors()) return ErrorResult.build(errors);
        return CustomResult.save(service.patchStatus(id, request));
    }
}
