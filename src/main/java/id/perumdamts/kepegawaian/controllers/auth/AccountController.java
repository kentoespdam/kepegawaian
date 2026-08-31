package id.perumdamts.kepegawaian.controllers.auth;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.appwrite.MeResponse;
import id.perumdamts.kepegawaian.dto.commons.CustomResult;
import id.perumdamts.kepegawaian.dto.commons.SingleResult;
import id.perumdamts.kepegawaian.exceptions.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * GET /account/me — roles + permissions user login untuk UI berbasis permission.
 * Sumber data: authorities principal (ROLE_* = role, ENTITY:ACTION = permission)
 * yang sudah di-inflate oleh JwtAuthFilter/DevAuthFilter (ADR-0037) — tanpa query DB.
 */
@Tag(name = "Account — Account")
@RestController
@RequestMapping("/account")
public class AccountController {

    @Operation(summary = "me")
    @GetMapping("/me")
    public ResponseEntity<SingleResult<MeResponse>> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth != null && auth.getPrincipal() instanceof AppwriteUser user)) {
            throw new NotFoundException("User tidak ditemukan");
        }
        List<String> roles = new ArrayList<>();
        List<String> permissions = new ArrayList<>();
        for (GrantedAuthority authority : auth.getAuthorities()) {
            String value = authority.getAuthority();
            assert value != null;
            if (value.startsWith("ROLE_")) {
                roles.add(value.substring("ROLE_".length()));
            } else {
                permissions.add(value);
            }
        }
        MeResponse response = new MeResponse(user.get$id(), user.getName(),
                roles.stream().sorted().toList(), permissions.stream().sorted().toList());
        return CustomResult.any(response);
    }
}
