package id.perumdamts.kepegawaian.controllers.auth;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.appwrite.MeResponse;
import id.perumdamts.kepegawaian.dto.appwrite.Prefs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test untuk {@link AccountController#me()} — partisi authorities
 * (ROLE_* vs ENTITY:ACTION) tanpa Spring context.
 */
class AccountControllerTest {

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void me_returnsSortedRolesAndPermissionsFromAuthorities() {
        Prefs prefs = new Prefs();
        prefs.setRoles(Set.of("ADMIN", "HRD"));
        // @AllArgsConstructor Lombok melewati field ber-@Getter(onMethod_=...) ($id, createdAt, updatedAt)
        AppwriteUser user = new AppwriteUser();
        user.set$id("123");
        user.setName("Budi");
        user.setPrefs(prefs);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>(user.getAuthorities());
        authorities.addAll(List.of(
                new SimpleGrantedAuthority("PEGAWAI:READ"),
                new SimpleGrantedAuthority("PROFIL:APPROVE"),
                new SimpleGrantedAuthority("MASTER:DELETE")));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, authorities));

        MeResponse data = new AccountController().me().getBody().getData();

        assertEquals("123", data.id());
        assertEquals("Budi", data.name());
        assertEquals(List.of("ADMIN", "HRD"), data.roles(), "roles dari authority ROLE_*, sorted");
        assertEquals(List.of("MASTER:DELETE", "PEGAWAI:READ", "PROFIL:APPROVE"),
                data.permissions(), "permissions dari authority ENTITY:ACTION, sorted");
    }
}
