package id.perumdamts.kepegawaian.services.revInfo;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import id.perumdamts.kepegawaian.dto.appwrite.Prefs;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
@Slf4j
//@RequiredArgsConstructor
class SyncExistProfilKeluargaTest {
    @Autowired
    private SyncExistProfilKeluarga service;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @BeforeEach
    public void setup() {
        List<PrefRole> roles = List.of(new PrefRole("ADMIN"));
        Prefs prefs = new Prefs();
        prefs.setRoles(roles.stream().map(PrefRole::getId).collect(Collectors.toSet()));

        AppwriteUser user = new AppwriteUser();
        user.set$id("Dev");
        user.setName("SYSTEM");
        user.setPrefs(prefs);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(user);
    }

    @Test
    void syncAudit() {
        service.syncAudit();
    }

    @Test
    void getProfilKeluargaHistory() {
    }
}