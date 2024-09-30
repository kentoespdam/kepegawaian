package id.perumdamts.kepegawaian.services.kepegawaian.riwayatKontrak;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.appwrite.Prefs;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakPostRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakPutRequest;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatKontrak.RiwayatKontrakResponse;
import id.perumdamts.kepegawaian.entities.commons.EJenisKontrak;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatKontrak;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Slf4j
class RiwayatKontrakServiceImplTest {
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @Autowired
    private RiwayatKontrakService service;

    RiwayatKontrakPostRequest postRequest;
    RiwayatKontrakPutRequest putRequest;

    private void setupAuthentication() {
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

    private void setupPostRequest() {
        postRequest = new RiwayatKontrakPostRequest();
        postRequest.setJenisKontrak(EJenisKontrak.PERPANJANGAN);
        postRequest.setPegawaiId(1L);
        postRequest.setNipam("KO-001");
        postRequest.setNama("Test Kontrak");
        postRequest.setNomorKontrak("KO/01/2022");
        postRequest.setTanggalSk(LocalDate.of(2022, 1, 28));
        postRequest.setTanggalMulai(LocalDate.of(2022, 2, 1));
        postRequest.setTanggalSelesai(LocalDate.of(2023, 2, 1));
        postRequest.setIsLatest(false);
        postRequest.setNotes("Test Perpanjangan Kontrak");
    }

    private void setupPutRequest() {
        putRequest = new RiwayatKontrakPutRequest();
        putRequest.setJenisKontrak(EJenisKontrak.PERPANJANGAN);
        putRequest.setPegawaiId(1L);
        putRequest.setNipam("KO-001");
        putRequest.setNama("Test Kontrak");
        putRequest.setNomorKontrak("KO/01/2022");
        putRequest.setTanggalSk(LocalDate.of(2022, 1, 28));
        putRequest.setTanggalMulai(LocalDate.of(2022, 2, 1));
        putRequest.setTanggalSelesai(LocalDate.of(2023, 2, 1));
        putRequest.setIsLatest(true);
        putRequest.setNotes("Update Test Perpanjangan Kontrak");
    }

    @BeforeEach
    public void setup() {
        setupAuthentication();
        setupPostRequest();
        setupPutRequest();
    }

    @Transactional
//    @Test
    public void createPerpanjangan() {
        SavedStatus<?> save = service.save(postRequest);
        assertNotNull(save);
        RiwayatKontrak data = (RiwayatKontrak) save.getData();
        assertNotNull(data);
        log.info("save: {}", data);
        Long id = data.getId();
        assertNotNull(id);
        log.info("id: {}", id);

        SavedStatus<?> update = service.update(id, putRequest);
        assertNotNull(update);
        RiwayatKontrak updatedData = (RiwayatKontrak) update.getData();
        log.info("update: {}", updatedData);
        assertNotNull(updatedData);

        boolean delete = service.delete(id);
        assertTrue(delete);
        RiwayatKontrakResponse byId = service.findById(id);
        assertNull(byId);
    }
}