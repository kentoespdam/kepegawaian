package id.perumdamts.kepegawaian.services.pegawai;

import id.perumdamts.kepegawaian.dto.appwrite.Prefs;
import id.perumdamts.kepegawaian.dto.commons.ESaveStatus;
import id.perumdamts.kepegawaian.dto.commons.SavedStatus;
import id.perumdamts.kepegawaian.dto.kepegawaian.riwayatSk.RiwayatSkResponse;
import id.perumdamts.kepegawaian.dto.pegawai.PegawaiPostRequest;
import id.perumdamts.kepegawaian.dto.pegawai.PegawaiResponse;
import id.perumdamts.kepegawaian.dto.pegawai.PegawaiResponseDetail;
import id.perumdamts.kepegawaian.entities.commons.*;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatKontrak;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSk;
import id.perumdamts.kepegawaian.entities.master.*;
import id.perumdamts.kepegawaian.entities.pegawai.Pegawai;
import id.perumdamts.kepegawaian.entities.profil.Biodata;
import id.perumdamts.kepegawaian.repositories.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.master.*;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatKontrak.GenericKontrakService;
import id.perumdamts.kepegawaian.services.kepegawaian.riwayatSk.RiwayatSkService;
import id.perumdamts.kepegawaian.services.profil.biodata.BiodataService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
//@AutoConfigureMockMvc
@Slf4j
class PegawaiServiceImplTest {
//    @Mock
//    private SecurityContext securityContext;
//    @Mock
//    private Authentication authentication;

    @Autowired
    private PegawaiServiceImpl service;
    @Autowired
    private PegawaiRepository repository;
    @Autowired
    private JabatanRepository jabatanRepository;
    @Autowired
    private OrganisasiRepository organisasiRepository;
    @Autowired
    private ProfesiRepository profesiRepository;
    @Autowired
    private GolonganRepository golonganRepository;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private BiodataService biodataService;
    @Autowired
    private RiwayatSkService riwayatSkService;
    @Autowired
    private GenericKontrakService genericKontrakService;

    private PegawaiPostRequest request;

    private void setupRequest() {
        /*
{
    "nik": "1234567890123450",
    "nama": "Test Kontrak",
    "jenisKelamin": "LAKI_LAKI",
    "tempatLahir": "BANYUMAS",
    "tanggalLahir": "2000-09-14",
    "alamat": "umah",
    "telp": "081234567890",
    "agama": "ISLAM",
    "ibuKandung": "BIYUNGE",
    "pendidikanTerakhirId": 7,
    "golonganDarah": "B",
    "statusKawin": "BELUM_KAWIN",
    "notes": "coba test tenaga kontrak",
    "statusPegawai": "KONTRAK",
    "nipam": "KO-001",
    "statusKerja": "KARYAWAN_AKTIF",
    "organisasiId": 39,
    "jabatanId": 59,
    "profesiId": 44,
    "gradeId": 8,
    "golonganId": 0,
    "nomorSk": "01/01/2024",
    "tanggalSk": "2024-01-24",
    "tmtBerlakuSk": "2024-02-01",
    "tmtKontrakSelesai": "2026-02-01",
    "gajiPokok": 1250000
  }
 */
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
        request.setGradeId(8L);
        request.setNomorSk("01/01/2024");
        request.setTanggalSk(LocalDate.of(2024, 1, 24));
        request.setTmtBerlakuSk(LocalDate.of(2024, 2, 1));
        request.setTmtKontrakSelesai(LocalDate.of(2026, 2, 1));
        request.setGajiPokok(1_250_000D);
    }

    @BeforeEach
    public void setup() {
//        openMocks(this);
        List<String> roles = List.of("ADMIN");
        Prefs prefs = new Prefs();
        prefs.setRoles(roles);

//        AppwriteUser user = new AppwriteUser();
//        user.set$id("ADMIN");
//        user.setName("Bagus Sudrajat");
//        user.setPrefs(prefs);
//
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.setContext(securityContext);
//        when(authentication.isAuthenticated()).thenReturn(true);
//        when(authentication.getPrincipal()).thenReturn(user);
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

    //    @Test
    @Transactional
    public void testSave() {
        try {
            if (request.getStatusPegawai().equals(EStatusPegawai.PEGAWAI)) request.setIsPegawai(true);
            Biodata biodata = biodataService.saveFromPegawai(request);
            if (request.getStatusPegawai().equals(EStatusPegawai.NON_PEGAWAI)) {
                assertEquals(EStatusPegawai.NON_PEGAWAI, request.getStatusPegawai());
                log.info("Success Non Pegawai : {}", biodata);
                return;
            }
            assertNotEquals(EStatusPegawai.NON_PEGAWAI, request.getStatusPegawai());
            boolean exists = repository.exists(request.getSpecificationPegawai());
            assertFalse(exists);
//            if (exists) {
//                log.error("Error : {} Pegawai sudah ada", ESaveStatus.DUPLICATE);
//                return;
//            }

            Jabatan jabatan = jabatanRepository.findById(request.getJabatanId())
                    .orElseThrow(() -> new RuntimeException("Unknown Jabatan"));
            Organisasi organisasi = organisasiRepository.findById(request.getOrganisasiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Organisasi"));
            Profesi profesi = profesiRepository.findById(request.getProfesiId())
                    .orElseThrow(() -> new RuntimeException("Unknown Profesi"));
            Golongan golongan = null;
            if (!request.getStatusPegawai().equals(EStatusPegawai.KONTRAK)) {
                golongan = golonganRepository.findById(request.getGolonganId())
                        .orElseThrow(() -> new RuntimeException("Unknown Golongan"));
            }
            Grade grade = gradeRepository.findById(request.getGradeId())
                    .orElseThrow(() -> new RuntimeException("Unknown Grade"));

            Pegawai entity = PegawaiPostRequest.toEntity(
                    request,
                    biodata,
                    jabatan,
                    organisasi,
                    profesi,
                    golongan,
                    grade
            );
            Pegawai save = repository.save(entity);
            Pegawai pegawai;
            if (!request.getStatusPegawai().equals(EStatusPegawai.KONTRAK))
                pegawai = saveCapeg(request, save);
            else {
                pegawai = saveKontrak(request, save);
//                pegawai = save;
            }
            assertNotNull(pegawai);
            log.info("Success save pegawai : {}", PegawaiResponse.from(pegawai));

        } catch (Exception e) {
            log.error("Error Save Pegawai : {}", e.getMessage());
            assertNull(e);
        }
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
