package id.perumdamts.kepegawaian.config.security;

import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import id.perumdamts.kepegawaian.entities.system.PrefPermission;
import id.perumdamts.kepegawaian.repositories.PrefRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link JwtAuthFilter#inflatePermissions} (ADR-0037 permission inflation).
 * Mocks {@link PrefRoleRepository} — no database required.
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthFilterPermissionInflationTest {

    @Mock
    private JwtTokenService service;
    @Mock
    private PrefRoleRepository prefRoleRepository;

    private JwtAuthFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthFilter(service, prefRoleRepository);
    }

    @Test
    void inflatesUnionOfPermissionsAcrossRolesDeduplicated() {
        PrefRole admin = new PrefRole("ADMIN");
        admin.getPermissions().addAll(Set.of(
                new PrefPermission("MASTER:DELETE"), new PrefPermission("PEGAWAI:READ")));
        PrefRole user = new PrefRole("USER");
        user.getPermissions().addAll(Set.of(
                new PrefPermission("PEGAWAI:READ"), new PrefPermission("PROFIL:UPDATE")));

        when(prefRoleRepository.findAllById(List.of("ADMIN", "USER"))).thenReturn(List.of(admin, user));

        List<String> result = filter.inflatePermissions(List.of("ADMIN", "USER")).stream()
                .map(SimpleGrantedAuthority::getAuthority)
                .toList();

        assertEquals(3, result.size(), "duplikat PEGAWAI:READ harus di-collapse");
        assertTrue(result.containsAll(List.of("MASTER:DELETE", "PEGAWAI:READ", "PROFIL:UPDATE")));
    }

    @Test
    void emptyRolesReturnNoPermissions() {
        assertTrue(filter.inflatePermissions(List.of()).isEmpty());
    }

    @Test
    void rolesMissingFromDbAreSkipped() {
        PrefRole admin = new PrefRole("ADMIN");
        admin.getPermissions().add(new PrefPermission("CUTI:APPROVE"));

        when(prefRoleRepository.findAllById(List.of("ADMIN", "GHOST"))).thenReturn(List.of(admin));

        List<String> result = filter.inflatePermissions(List.of("ADMIN", "GHOST")).stream()
                .map(SimpleGrantedAuthority::getAuthority)
                .toList();

        assertEquals(List.of("CUTI:APPROVE"), result);
    }
}
