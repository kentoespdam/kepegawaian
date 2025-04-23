package id.perumdamts.kepegawaian.services.pegawai;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import id.perumdamts.kepegawaian.dto.appwrite.Prefs;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.pegawai.PegawaiPostRequest;
import id.perumdamts.kepegawaian.entities.commons.*;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
@Slf4j
class PegawaiServiceTest {
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @Autowired
    private PegawaiService service;

    PegawaiPostRequest postRequest;

    private void setupAuthentication() {
        List<PrefRole> roles = List.of(new PrefRole("ADMIN"));
        Prefs prefs = new Prefs();
        prefs.setRoles(roles.stream().map(PrefRole::getId).collect(Collectors.toSet()));

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
        postRequest = new PegawaiPostRequest();
        postRequest.setNik("8527419361234567891");
        postRequest.setNama("Uji Coba Input Tenaga Kontrak");
        postRequest.setJenisKelamin(EJenisKelamin.LAKI_LAKI);
        postRequest.setTempatLahir("Jakarta");
        postRequest.setTanggalLahir(LocalDate.of(2000, 1, 1));
        postRequest.setAlamat("Jl. Jalan");
        postRequest.setTelp("081234567890");
        postRequest.setAgama(EAgama.ISLAM);
        postRequest.setIbuKandung("Uji Coba Input Ibu Kandung");
        postRequest.setPendidikanTerakhirId(7L);
        postRequest.setGolonganDarah(EGolonganDarah.B);
        postRequest.setStatusKawin(EStatusKawin.BELUM_KAWIN);
        postRequest.setNotes("coba test tenaga kontrak");
        postRequest.setNipam("KO-001");
        postRequest.setStatusPegawai(EStatusPegawai.KONTRAK);
        postRequest.setStatusKerja(EStatusKerja.KARYAWAN_AKTIF);
        postRequest.setOrganisasiId(39L);
        postRequest.setJabatanId(59L);
        postRequest.setProfesiId(44L);
        postRequest.setGolonganId(0L);
        postRequest.setNomorSk("01/01/2024");
        postRequest.setTanggalSk(LocalDate.of(2024, 1, 1));
        postRequest.setTmtBerlakuSk(LocalDate.of(2024, 2, 1));
        postRequest.setTmtKontrakSelesai(LocalDate.of(2026, 2, 1));
        postRequest.setGajiPokok(1_250_000D);
    }

    @BeforeEach
    void setup() {
        setupAuthentication();
        setupPostRequest();
    }

    @Transactional
//    @Test
    void createKontrak() {
        SavedStatus<?> save = service.save(postRequest);
        assertNotNull(save);
        Pegawai pegawai = (Pegawai) save.getData();
        log.info("save: {}", pegawai);
        assertNotNull(pegawai);
        Long id = pegawai.getId();
        assertNotNull(id);
        log.info("id: {}", id);
    }
}