package id.perumdamts.kepegawaian.entities.master;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.appwrite.Prefs;
import id.perumdamts.kepegawaian.repositories.master.GolonganRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@SpringBootTest
@Slf4j
class GolonganTest {
    @Mock
    private GolonganRepository repository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    private Golongan golongan;

    @BeforeEach
    public void setup() {
        openMocks(this);
        List<String> roles = List.of("ADMIN");
        Prefs prefs = new Prefs();
        prefs.setRoles(roles);

        AppwriteUser user = new AppwriteUser();
        user.set$id("900800456");
        user.setName("Bagus Sudrajat");
        user.setPrefs(prefs);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(user);

        golongan = new Golongan();
        golongan.setId(1L);
        golongan.setGolongan("1");
        golongan.setPangkat("Pangkat 1");
        golongan.setIsDeleted(false);
    }

//    @Test
    void testDelete() {
        when(repository.findById(1L)).thenReturn(Optional.of(golongan));
        log.info("before delete: {}", golongan);
        assertFalse(golongan.getIsDeleted());
        golongan.setIsDeleted(true);
        repository.save(golongan);
        log.info("after delete: {}", golongan);
        assertTrue(golongan.getIsDeleted());
        verify(repository, times(1)).save(golongan);
    }
}