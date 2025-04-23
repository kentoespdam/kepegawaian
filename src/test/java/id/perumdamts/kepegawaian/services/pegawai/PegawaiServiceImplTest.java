package id.perumdamts.kepegawaian.services.pegawai;

import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import id.perumdamts.kepegawaian.dto.appwrite.PrefRole;
import id.perumdamts.kepegawaian.dto.appwrite.Prefs;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkResponse;
import id.perumdamts.kepegawaian.dto.pegawai.PegawaiPostRequest;
import id.perumdamts.kepegawaian.dto.pegawai.PegawaiResponseDetail;
import id.perumdamts.kepegawaian.entities.commons.*;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatKontrak;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.repositories.PegawaiRepository;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatKontrak.GenericKontrakService;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatSk.RiwayatSkService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;


@SpringBootTest
//@AutoConfigureMockMvc
@Slf4j
class PegawaiServiceImplTest {
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @Autowired
    private PegawaiServiceImpl service;
    @Autowired
    private PegawaiRepository repository;
    @Autowired
    private RiwayatSkService riwayatSkService;
    @Autowired
    private GenericKontrakService genericKontrakService;

    private PegawaiPostRequest request;

    private void setupRequest() {
        request = new PegawaiPostRequest();
        request.setNik("1234567890123450");
        request.setNama("Test Kontrak");
        request.setJenisKelamin(EJenisKelamin.LAKI_LAKI);
        request.setTempatLahir("BANYUMAS");
        request.setTanggalLahir(LocalDate.of(2000, 9, 14));
        request.setAlamat("umah");
        request.setTelp("081234567890");
        request.setAgama(EAgama.ISLAM);
        request.setIbuKandung("BIYUNGE");
        request.setPendidikanTerakhirId(7L);
        request.setGolonganDarah(EGolonganDarah.B);
        request.setStatusKawin(EStatusKawin.BELUM_KAWIN);
        request.setNotes("coba test tenaga kontrak");
        request.setStatusPegawai(EStatusPegawai.KONTRAK);
        request.setNipam("KO-001");
        request.setStatusKerja(EStatusKerja.KARYAWAN_AKTIF);
        request.setOrganisasiId(39L);
        request.setJabatanId(59L);
        request.setProfesiId(44L);
        request.setNomorSk("01/01/2024");
        request.setTanggalSk(LocalDate.of(2024, 1, 24));
        request.setTmtBerlakuSk(LocalDate.of(2024, 2, 1));
        request.setTmtKontrakSelesai(LocalDate.of(2026, 2, 1));
        request.setGajiPokok(1_250_000D);
    }

    @BeforeEach
    public void setup() {
//        openMocks(this);
        List<PrefRole> roles = List.of(new PrefRole("ADMIN"));
        Prefs prefs = new Prefs();
        prefs.setRoles(roles.stream().map(PrefRole::getId).collect(Collectors.toSet()));

        AppwriteUser user = new AppwriteUser();
        user.set$id("ADMIN");
        user.setName("Bagus Sudrajat");
        user.setPrefs(prefs);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(user);
//
//        setupRequest();
    }

    @Transactional
//    @Test
    public void test() {
        SavedStatus<?> save = service.save(request);
        assertEquals(ESaveStatus.SUCCESS, save.getStatus());
        Pegawai data = (Pegawai) save.getData();
        Long id = data.getId();
        assertNotNull(id);

        PegawaiResponseDetail byId = service.findById(id);
        assertNotNull(byId);
        log.info("pegawai : {}", byId);
    }

    private Pegawai saveCapeg(PegawaiPostRequest request, Pegawai pegawai) {
        RiwayatSk riwayatSk = riwayatSkService.saveCapeg(request, pegawai);
        pegawai.setRefSkCapegId(riwayatSk.getId());
        pegawai.setMkgTahun(0);
        pegawai.setMkgBulan(0);

        return repository.save(pegawai);
    }

    private Pegawai saveKontrak(PegawaiPostRequest request, Pegawai pegawai) {
        RiwayatKontrak riwayatKontrak = genericKontrakService.saveFromPegawai(request, pegawai);
        assertNotNull(riwayatKontrak);
        return pegawai;
    }

//    @Transactional
//    @Test
    public void detail() {
        Long id = 1L;
        Optional<PegawaiResponseDetail> pegawaiResponseDetail = repository.findById(id).map(pegawai -> {
            List<Long> riwayatIds = new ArrayList<>();
            if (Objects.nonNull(pegawai.getRefSkCapegId())) riwayatIds.add(pegawai.getRefSkCapegId());
            if (Objects.nonNull(pegawai.getRefSkPegawaiId())) riwayatIds.add(pegawai.getRefSkPegawaiId());
            if (Objects.nonNull(pegawai.getRefSkGolId())) riwayatIds.add(pegawai.getRefSkGolId());
            if (Objects.nonNull(pegawai.getRefSkJabatanId())) riwayatIds.add(pegawai.getRefSkJabatanId());
            if (Objects.nonNull(pegawai.getRefSkMutasiId())) riwayatIds.add(pegawai.getRefSkMutasiId());
            List<RiwayatSkResponse> riwayatSkResponses = riwayatSkService.findByIds(riwayatIds);
            log.info("list riwayat : {}", riwayatSkResponses);
            return PegawaiResponseDetail.from(pegawai, riwayatSkResponses);
        });
        assertNotNull(pegawaiResponseDetail);
    }
}
