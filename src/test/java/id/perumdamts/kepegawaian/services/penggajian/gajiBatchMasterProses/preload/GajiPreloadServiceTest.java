package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses.preload;

import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import id.perumdamts.kepegawaian.entities.commons.EJenisTunjangan;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.penggajian.*;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jpa.RiwayatSpRepository;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchPotonganTkkRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.ObjectProvider;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GajiPreloadServiceTest {

    @Mock
    private GajiKomponenRepository gajiKomponenRepository;
    @Mock
    private GajiTunjanganRepository gajiTunjanganRepository;
    @Mock
    private GajiParameterSettingRepository gajiParameterSettingRepository;
    @Mock
    private GajiPendapatanNonPajakRepository gajiPendapatanNonPajakRepository;
    @Mock
    private GajiPotonganTkkRepository gajiPotonganTkkRepository;
    @Mock
    private PegawaiRepository pegawaiRepository;
    @Mock
    private GajiBatchPotonganTkkRepository gajiBatchPotonganTkkRepository;
    @Mock
    private GajiKpiRepository gajiKpiRepository;
    @Mock
    private RiwayatSpRepository riwayatSpRepository;
    @Mock
    private ObjectProvider<GajiPreloadService> selfProvider;

    private GajiPreloadService preloadService;

    @BeforeEach
    void setUp() {
        preloadService = new GajiPreloadService(
                gajiKomponenRepository,
                gajiTunjanganRepository,
                gajiParameterSettingRepository,
                gajiPendapatanNonPajakRepository,
                gajiPotonganTkkRepository,
                pegawaiRepository,
                gajiBatchPotonganTkkRepository,
                gajiKpiRepository,
                riwayatSpRepository,
                selfProvider
        );
        when(selfProvider.getIfAvailable(any())).thenReturn(preloadService);
    }

    @Test
    void fetchKomponenAllProfil_groupsByProfilId() {
        GajiProfil profil1 = new GajiProfil(1L, "Profil 1");
        GajiProfil profil2 = new GajiProfil(2L, "Profil 2");

        GajiKomponen k1 = new GajiKomponen();
        k1.setUrut(1);
        k1.setProfilGaji(profil1);
        k1.setKode("GP");
        k1.setNama("Gaji Pokok");
        k1.setJenisGaji(EJenisGaji.PEMASUKAN);
        k1.setNilai(0.0);
        k1.setIsReference(true);
        k1.setFormula("#SYSTEM");

        GajiKomponen k2 = new GajiKomponen();
        k2.setUrut(2);
        k2.setProfilGaji(profil2);
        k2.setKode("TUNJ");
        k2.setNama("Tunjangan");
        k2.setJenisGaji(EJenisGaji.PEMASUKAN);
        k2.setNilai(1000.0);
        k2.setIsReference(false);
        k2.setFormula(null);

        when(gajiKomponenRepository.findByOrderByUrutAsc()).thenReturn(List.of(k1, k2));

        Map<Long, List<GajiKomponen>> map = preloadService.fetchKomponenAllProfil();
        assertEquals(1, map.get(1L).size());
        assertEquals("GP", map.get(1L).get(0).getKode());
        assertEquals(1, map.get(2L).size());
        assertEquals("TUNJ", map.get(2L).get(0).getKode());
    }

    @Test
    void fetchParameterSettings_mapsByKode() {
        GajiParameterSetting p1 = new GajiParameterSetting(1L, "maksimal_potongan_jpn", 100_000.0);
        GajiParameterSetting p2 = new GajiParameterSetting(2L, "maksimal_potongan_askes", 200_000.0);
        when(gajiParameterSettingRepository.findAll()).thenReturn(List.of(p1, p2));

        Map<String, Double> map = preloadService.fetchParameterSettings();
        assertEquals(100_000.0, map.get("maksimal_potongan_jpn"));
        assertEquals(200_000.0, map.get("maksimal_potongan_askes"));
    }

    @Test
    void fetchPtkp_mapsById() {
        GajiPendapatanNonPajak ptkp = new GajiPendapatanNonPajak(10L, "TK/0", 54_000_000.0, "notes");
        when(gajiPendapatanNonPajakRepository.findAll()).thenReturn(List.of(ptkp));

        Map<Long, Double> map = preloadService.fetchPtkp();
        assertEquals(54_000_000.0, map.get(10L));
    }

    @Test
    void fetchTunjangan_partitionsLevelAndGolongan() {
        Level level = new Level();
        level.setId(5L);
        Golongan gol = new Golongan();
        gol.setId(3L);

        GajiTunjangan tLevel = new GajiTunjangan(EJenisTunjangan.JABATAN, level, 1_500_000.0);
        GajiTunjangan tGol = new GajiTunjangan(EJenisTunjangan.AIR, null, gol, 50_000.0);

        when(gajiTunjanganRepository.findAll()).thenReturn(List.of(tLevel, tGol));

        GajiPreloadContext.PreloadTunjanganData data = preloadService.fetchTunjangan();
        assertEquals(1_500_000.0, data.byJenisAndLevel().get(new GajiPreloadContext.JenisLevelKey(EJenisTunjangan.JABATAN, 5L)));
        assertEquals(50_000.0, data.byJenisAndGolongan().get(new GajiPreloadContext.JenisGolonganKey(EJenisTunjangan.AIR, 3L)));
    }

    @Test
    void preload_integratesLiveAndCachedData() {
        Level level = new Level();
        level.setId(5L);

        GajiTunjangan tLevel = new GajiTunjangan(EJenisTunjangan.JABATAN, level, 1_500_000.0);
        when(gajiTunjanganRepository.findAll()).thenReturn(List.of(tLevel));
        when(gajiKomponenRepository.findByOrderByUrutAsc()).thenReturn(List.of());
        when(gajiParameterSettingRepository.findAll()).thenReturn(List.of(
                new GajiParameterSetting(1L, "maksimal_potongan_jpn", 100_000.0)
        ));
        when(gajiPendapatanNonPajakRepository.findAll()).thenReturn(List.of());
        when(gajiPotonganTkkRepository.findAll()).thenReturn(List.of());

        // Mock Live
        when(pegawaiRepository.findIsAskesByIdIn(Set.of(99L))).thenReturn(List.<Object[]>of(new Object[]{99L, true}));
        when(pegawaiRepository.findRumahDinasNilaiByIdIn(Set.of(99L))).thenReturn(List.<Object[]>of(new Object[]{99L, 250_000.0}));
        when(gajiBatchPotonganTkkRepository.sumPotonganGroupByNipam("B001")).thenReturn(List.<Object[]>of(new Object[]{"NIP001", 75_000L}));
        GajiKpi kpi = new GajiKpi();
        kpi.setNipam("NIP001");
        kpi.setTunkin(800_000.0);
        when(gajiKpiRepository.findByPeriodeAndNipamIn(eq("2026-09"), any())).thenReturn(List.of(kpi));
        when(riwayatSpRepository.findAllPegawaiIdsWithActiveSp3In(eq(Set.of(99L)), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Set.of(99L));

        GajiBatchMaster master = new GajiBatchMaster();
        master.setPegawaiId(99L);
        master.setNipam("NIP001");
        master.setGajiPokok(4_000_000.0);
        master.setJmlTanggungan(2);
        master.setStatusKawin(EStatusKawin.KAWIN);
        master.setLevelId(5L);

        GajiPreloadContext ctx = preloadService.preload("B001", "202609", List.of(master));

        assertNotNull(ctx);
        assertEquals(4_000_000.0, ctx.resolve("GP", master, Map.of()));
        assertEquals(2.0, ctx.resolve("JML_ANAK", master, Map.of()));
        assertEquals(4.0, ctx.resolve("JML_JIWA", master, Map.of())); // 1 + 2 + 1 = 4
        assertEquals(1.0, ctx.resolve("REF_ASKES", master, Map.of()));
        assertEquals(250_000.0, ctx.resolve("REF_SEWA_RUMDIN", master, Map.of()));
        assertEquals(75_000.0, ctx.resolve("REF_JML_POT_KK", master, Map.of()));
        assertEquals(800_000.0, ctx.resolve("TUNJ_KINERJA", master, Map.of()));
        assertTrue(ctx.isSp3Aktif(master));
        assertEquals(0.0, ctx.resolve("REF_TUNJ_KK", master, Map.of())); // SP-3 aktif -> tunj kinerja 0
        assertEquals(1_500_000.0, ctx.resolve("REF_TUNJ_JABATAN", master, Map.of()));
        assertEquals(100_000.0, ctx.clampPotongan("POT_JP", 150_000.0));
    }

    @Test
    void hitungPegawaiResult_isSuccessHelper() {
        GajiBatchMaster master = new GajiBatchMaster();
        HitungPegawaiResult success = new HitungPegawaiResult(master, List.of(), null);
        assertTrue(success.isSuccess());

        ErrorEntry error = new ErrorEntry("NIP001", "Nama", null, "Formula error");
        HitungPegawaiResult failed = new HitungPegawaiResult(master, List.of(), error);
        assertFalse(failed.isSuccess());
        assertEquals("Formula error", failed.error().notes());
    }
}
