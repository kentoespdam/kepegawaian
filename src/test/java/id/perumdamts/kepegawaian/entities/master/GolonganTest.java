package id.perumdamts.kepegawaian.entities.master;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.appwrite.Prefs;
import id.perumdamts.kepegawaian.repositories.master.GolonganRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@SpringBootTest
class GolonganTest {
    @Autowired
    private GolonganRepository repository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @BeforeEach
    public void setup() {
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
    }
    @Test
    void test() {
        // create test with jwt authentication

        Golongan golongan = new Golongan();
        golongan.setGolongan("1");
        golongan.setPangkat("Pangkat 1");
        Golongan gol = repository.save(golongan);
        assertNotNull(gol);
        Long id=gol.getId();
        System.out.println("golongan id: "+id);

        gol.setIsDeleted(true);

        repository.save(gol);
        Golongan byId = repository.findById(id).orElse(null);
        assertNull(byId);
    }
}