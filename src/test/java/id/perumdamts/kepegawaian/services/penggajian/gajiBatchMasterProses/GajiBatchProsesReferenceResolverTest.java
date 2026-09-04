package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses;

import id.perumdamts.kepegawaian.entities.commons.EJenisTunjangan;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.master.Golongan;
import id.perumdamts.kepegawaian.entities.master.Level;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import id.perumdamts.kepegawaian.entities.penggajian.GajiKpi;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPendapatanNonPajak;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPotonganTkk;
import id.perumdamts.kepegawaian.entities.penggajian.GajiTunjangan;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jpa.RiwayatSpRepository;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchPotonganTkkRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiKpiRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiPotonganTkkRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiTunjanganRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test {@link GajiBatchProsesReferenceResolver} — mock tiap repository,
 * snapshot {@link GajiBatchMaster} di-mock (getter-only). Wave 4-1.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GajiBatchProsesReferenceResolverTest {

    private static final String BATCH_ID = "202509-001";

    @Mock
    private GajiTunjanganRepository gajiTunjanganRepository;
    @Mock
    private GajiPotonganTkkRepository gajiPotonganTkkRepository;
    @Mock
    private GajiBatchPotonganTkkRepository gajiBatchPotonganTkkRepository;
    @Mock
    private GajiKpiRepository gajiKpiRepository;
    @Mock
    private RiwayatSpRepository riwayatSpRepository;
    @Mock
    private PegawaiRepository pegawaiRepository;

    private GajiBatchProsesReferenceResolver resolver;
    private Map<String, Double> ctx;

    @BeforeEach
    void setUp() {
        resolver = new GajiBatchProsesReferenceResolver(
                gajiTunjanganRepository, gajiPotonganTkkRepository, gajiBatchPotonganTkkRepository,
                gajiKpiRepository, riwayatSpRepository, pegawaiRepository);
        ctx = new HashMap<>();
    }

    private GajiBatchMaster master() {
        GajiBatchMaster m = mock(GajiBatchMaster.class);
        when(m.getNipam()).thenReturn("ITKPI");
        when(m.getPegawaiId()).thenReturn(99L);
        when(m.getPeriode()).thenReturn("202509");
        when(m.getStatusPegawai()).thenReturn(EStatusPegawai.PEGAWAI);
        when(m.getStatusKawin()).thenReturn(EStatusKawin.KAWIN);
        when(m.getLevelId()).thenReturn(5L);
        when(m.getGolonganId()).thenReturn(3L);
        when(m.getGajiPokok()).thenReturn(5_000_000.0);
        when(m.getJmlTanggungan()).thenReturn(2);
        when(m.getPhdp()).thenReturn(500_000.0);
        return m;
    }

    // ---------- snapshot langsung ----------

    @Test
    void resolve_gp_dariSnapshot() {
        assertEquals(5_000_000.0, resolver.resolve("GP", master(), ctx, BATCH_ID), 0.001);
    }

    @Test
    void resolve_gp_nullDihitungNol() {
        GajiBatchMaster m = master();
        when(m.getGajiPokok()).thenReturn(null);
        assertEquals(0.0, resolver.resolve("GP", m, ctx, BATCH_ID), 0.001);
    }

    @Test
    void resolve_jmlAnak_dariSnapshot() {
        assertEquals(2.0, resolver.resolve("JML_ANAK", master(), ctx, BATCH_ID), 0.001);
    }

    @Test
    void resolve_jmlJiwa_1PlusAnakPlusKawin() {
        assertEquals(4.0, resolver.resolve("JML_JIWA", master(), ctx, BATCH_ID), 0.001);
    }

    @Test
    void resolve_jmlJiwa_belumKawin() {
        GajiBatchMaster m = master();
        when(m.getStatusKawin()).thenReturn(EStatusKawin.BELUM_KAWIN);
        assertEquals(3.0, resolver.resolve("JML_JIWA", m, ctx, BATCH_ID), 0.001);
    }

    @Test
    void resolve_refPtkp_dariRelasiSnapshot() {
        GajiPendapatanNonPajak ptkp = new GajiPendapatanNonPajak(1L, "K0", 54_000_000.0, null);
        GajiBatchMaster m = master();
        when(m.getGajiPendapatanNonPajakId()).thenReturn(ptkp);
        assertEquals(54_000_000.0, resolver.resolve("REF_PTKP", m, ctx, BATCH_ID), 0.001);
    }

    @Test
    void resolve_refPtkp_nullRelasi() {
        assertEquals(0.0, resolver.resolve("REF_PTKP", master(), ctx, BATCH_ID), 0.001);
    }

    @Test
    void resolve_refPhdp_dariSnapshot() {
        assertEquals(500_000.0, resolver.resolve("REF_PHDP", master(), ctx, BATCH_ID), 0.001);
    }

    // ---------- live Pegawai (keputusan user: tidak di-snapshot) ----------

    @Test
    void resolve_refAskes_askesTrue() {
        when(pegawaiRepository.findIsAskesById(99L)).thenReturn(Optional.of(true));
        assertEquals(1.0, resolver.resolve("REF_ASKES", master(), ctx, BATCH_ID), 0.001);
    }

    @Test
    void resolve_refAskes_askesFalse() {
        when(pegawaiRepository.findIsAskesById(99L)).thenReturn(Optional.of(false));
        assertEquals(0.0, resolver.resolve("REF_ASKES", master(), ctx, BATCH_ID), 0.001);
    }

    @Test
    void resolve_refSewaRumdin_nilaiRumahDinas() {
        when(pegawaiRepository.findRumahDinasNilaiById(99L)).thenReturn(Optional.of(250_000.0));
        assertEquals(250_000.0, resolver.resolve("REF_SEWA_RUMDIN", master(), ctx, BATCH_ID), 0.001);
    }

    @Test
    void resolve_refSewaRumdin_tanpaRumahDinas() {
        when(pegawaiRepository.findRumahDinasNilaiById(99L)).thenReturn(Optional.empty());
        assertEquals(0.0, resolver.resolve("REF_SEWA_RUMDIN", master(), ctx, BATCH_ID), 0.001);
    }

    // ---------- lookup GajiPotonganTkk ----------

    @Test
    void resolve_refPotTkk_levelKeyed() {
        GajiPotonganTkk row = new GajiPotonganTkk(EStatusPegawai.PEGAWAI, new Level(5L), 113_500.0);
        when(gajiPotonganTkkRepository.findByStatusPegawaiAndLevelIdAndGolonganIsNull(EStatusPegawai.PEGAWAI, 5L))
                .thenReturn(Optional.of(row));
        assertEquals(113_500.0, resolver.resolve("REF_POT_TKK", master(), ctx, BATCH_ID), 0.001);
    }

    @Test
    void resolve_refPotTkk_golonganFallback() {
        GajiBatchMaster m = master();
        when(m.getLevelId()).thenReturn(7L);
        GajiPotonganTkk row = new GajiPotonganTkk(EStatusPegawai.PEGAWAI, 75_000.0);
        row.setGolongan(new Golongan(3L));
        when(gajiPotonganTkkRepository.findByStatusPegawaiAndLevelIdAndGolonganIsNull(EStatusPegawai.PEGAWAI, 7L))
                .thenReturn(Optional.empty());
        when(gajiPotonganTkkRepository.findByStatusPegawaiAndGolonganId(EStatusPegawai.PEGAWAI, 3L))
                .thenReturn(Optional.of(row));
        assertEquals(75_000.0, resolver.resolve("REF_POT_TKK", m, ctx, BATCH_ID), 0.001);
    }

    @Test
    void resolve_refPotTkk_flatByStatus() {
        GajiBatchMaster m = mock(GajiBatchMaster.class);
        when(m.getNipam()).thenReturn("HNR-1");
        when(m.getStatusPegawai()).thenReturn(EStatusPegawai.HONORER);
        when(m.getLevelId()).thenReturn(null);
        when(m.getGolonganId()).thenReturn(null);
        GajiPotonganTkk row = new GajiPotonganTkk(EStatusPegawai.HONORER, 75_000.0);
        when(gajiPotonganTkkRepository.findByStatusPegawaiAndLevelIsNullAndGolonganIsNull(EStatusPegawai.HONORER))
                .thenReturn(Optional.of(row));
        assertEquals(75_000.0, resolver.resolve("REF_POT_TKK", m, ctx, BATCH_ID), 0.001);
    }

    @Test
    void resolve_refPotTkk_tidakDitemukan() {
        when(gajiPotonganTkkRepository.findByStatusPegawaiAndLevelIdAndGolonganIsNull(any(), any()))
                .thenReturn(Optional.empty());
        when(gajiPotonganTkkRepository.findByStatusPegawaiAndGolonganId(any(), any()))
                .thenReturn(Optional.empty());
        when(gajiPotonganTkkRepository.findByStatusPegawaiAndLevelIsNullAndGolonganIsNull(any()))
                .thenReturn(Optional.empty());
        assertEquals(0.0, resolver.resolve("REF_POT_TKK", master(), ctx, BATCH_ID), 0.001);
    }

    // ---------- lookup GajiTunjangan ----------

    @Test
    void resolve_refTunjanganJabatan_levelKeyed() {
        GajiTunjangan row = new GajiTunjangan(EJenisTunjangan.JABATAN, new Level(5L), 1_500_000.0);
        when(gajiTunjanganRepository.findByJenisTunjanganAndLevelIdAndGolonganIsNull(EJenisTunjangan.JABATAN, 5L))
                .thenReturn(Optional.of(row));
        assertEquals(1_500_000.0, resolver.resolve("REF_TUNJ_JABATAN", master(), ctx, BATCH_ID), 0.001);
    }

    @Test
    void resolve_refTunjanganKinerja_golonganFallback() {
        GajiBatchMaster m = master();
        when(m.getLevelId()).thenReturn(7L);
        GajiTunjangan row = new GajiTunjangan(EJenisTunjangan.KINERJA, new Level(7L), new Golongan(3L), 1_650_000.0);
        when(gajiTunjanganRepository.findByJenisTunjanganAndLevelIdAndGolonganIsNull(EJenisTunjangan.KINERJA, 7L))
                .thenReturn(Optional.empty());
        when(gajiTunjanganRepository.findByJenisTunjanganAndGolonganId(EJenisTunjangan.KINERJA, 3L))
                .thenReturn(Optional.of(row));
        assertEquals(1_650_000.0, resolver.resolve("REF_TUNJ_KK", m, ctx, BATCH_ID), 0.001);
    }

    @Test
    void resolve_refTunjanganKinerja_sp3Aktif_jadiNol() {
        // window gaji 2025-09: [2025-08-21 .. 2025-09-20]
        when(riwayatSpRepository.existsSp3Aktif(99L, LocalDate.of(2025, 9, 20), LocalDate.of(2025, 8, 21)))
                .thenReturn(true);
        assertEquals(0.0, resolver.resolve("REF_TUNJ_KK", master(), ctx, BATCH_ID), 0.001);
    }

    @Test
    void resolve_refTunjanganKinerja_sp3TidakAktif() {
        GajiTunjangan row = new GajiTunjangan(EJenisTunjangan.KINERJA, new Level(5L), 2_500_000.0);
        when(riwayatSpRepository.existsSp3Aktif(99L, LocalDate.of(2025, 9, 20), LocalDate.of(2025, 8, 21)))
                .thenReturn(false);
        when(gajiTunjanganRepository.findByJenisTunjanganAndLevelIdAndGolonganIsNull(EJenisTunjangan.KINERJA, 5L))
                .thenReturn(Optional.of(row));
        assertEquals(2_500_000.0, resolver.resolve("REF_TUNJ_KK", master(), ctx, BATCH_ID), 0.001);
    }

    // ---------- lookup GajiBatchPotonganTkk ----------

    @Test
    void resolve_refJmlPotTkk_sumDariBatch() {
        when(gajiBatchPotonganTkkRepository.sumPotonganByBatchIdAndNipam(BATCH_ID, "ITKPI")).thenReturn(11L);
        assertEquals(11.0, resolver.resolve("REF_JML_POT_KK", master(), ctx, BATCH_ID), 0.001);
    }

    @Test
    void resolve_refJmlPotTkk_spellingTkk_jugaDikenali() {
        when(gajiBatchPotonganTkkRepository.sumPotonganByBatchIdAndNipam(BATCH_ID, "ITKPI")).thenReturn(5L);
        assertEquals(5.0, resolver.resolve("REF_JML_POT_TKK", master(), ctx, BATCH_ID), 0.001);
    }

    @Test
    void resolve_refJmlPotTkk_tanpaData_nol() {
        when(gajiBatchPotonganTkkRepository.sumPotonganByBatchIdAndNipam(BATCH_ID, "ITKPI")).thenReturn(null);
        assertEquals(0.0, resolver.resolve("REF_JML_POT_KK", master(), ctx, BATCH_ID), 0.001);
    }

    // ---------- lookup GajiKpi ----------

    @Test
    void resolve_tunjKinerja_dariKpi() {
        GajiKpi kpi = new GajiKpi();
        kpi.setTunkin(2_000_000.0);
        when(gajiKpiRepository.findByNipamAndPeriode("ITKPI", "2025-09")).thenReturn(Optional.of(kpi));
        assertEquals(2_000_000.0, resolver.resolve("TUNJ_KINERJA", master(), ctx, BATCH_ID), 0.001);
    }

    @Test
    void resolve_tunjKinerja_tanpaData_nol() {
        when(gajiKpiRepository.findByNipamAndPeriode("ITKPI", "2025-09")).thenReturn(Optional.empty());
        assertEquals(0.0, resolver.resolve("TUNJ_KINERJA", master(), ctx, BATCH_ID), 0.001);
    }
}