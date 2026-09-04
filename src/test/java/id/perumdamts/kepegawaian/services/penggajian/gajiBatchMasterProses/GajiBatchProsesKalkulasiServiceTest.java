package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses;

import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import id.perumdamts.kepegawaian.entities.commons.EJenisTunjangan;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.commons.EStatusPegawai;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMasterProses;
import id.perumdamts.kepegawaian.entities.penggajian.GajiKomponen;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPendapatanNonPajak;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses.preload.GajiPreloadContext;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses.preload.HitungPegawaiResult;
import id.perumdamts.kepegawaian.utils.GajiFormulaEvaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GajiBatchProsesKalkulasiServiceTest {

    private GajiBatchProsesKalkulasiService service;
    private GajiFormulaEvaluator formulaEvaluator;

    @BeforeEach
    void setUp() {
        formulaEvaluator = new GajiFormulaEvaluator();
        service = new GajiBatchProsesKalkulasiService(formulaEvaluator);
    }

    private GajiKomponen komponen(int urut, String kode, EJenisGaji jenis, boolean isRef, String formula) {
        return new GajiKomponen(urut, null, kode, kode, jenis, 0, isRef, formula);
    }

    private GajiBatchMaster master() {
        GajiBatchMaster m = new GajiBatchMaster();
        m.setId(1L);
        m.setGajiProfilId(1L);
        m.setNipam("12345");
        m.setNama("Budi");
        m.setStatusPegawai(EStatusPegawai.PEGAWAI);
        m.setStatusKawin(EStatusKawin.KAWIN);
        m.setLevelId(5L);
        m.setGajiPokok(5_000_000.0);
        m.setJmlTanggungan(2);
        return m;
    }

    private GajiPreloadContext createContext(
            List<GajiKomponen> komponens,
            Map<String, Double> parameterSettings,
            Map<GajiPreloadContext.JenisLevelKey, Double> tunjanganByJenisAndLevel
    ) {
        return new GajiPreloadContext(
                komponens != null ? Map.of(1L, komponens) : Map.of(),
                parameterSettings != null ? parameterSettings : Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                tunjanganByJenisAndLevel != null ? tunjanganByJenisAndLevel : Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Set.of()
        );
    }

    private GajiBatchMasterProses byKode(List<GajiBatchMasterProses> rows, String kode) {
        return rows.stream().filter(r -> r.getKode().equals(kode)).findFirst().orElseThrow();
    }

    /** Alur penuh profil 1: implicit resolve TUNJ_JABATAN, evaluasi formula, clamp, round, total master. */
    @Test
    void hitung_alurPenuh_profil1() {
        List<GajiKomponen> komponens = List.of(
                komponen(1, "GP", EJenisGaji.PEMASUKAN, true, "#SYSTEM"),
                komponen(2, "TUNJ_JABATAN", EJenisGaji.PEMASUKAN, false, ""),
                komponen(3, "PENGHASILAN_KOTOR", EJenisGaji.NONE, false, "GP + TUNJ_JABATAN"),
                komponen(4, "POT_JP", EJenisGaji.POTONGAN, false, "( GP + TUNJ_JABATAN ) * 0.01"),
                komponen(5, "POT_ASKES", EJenisGaji.POTONGAN, false, "PENGHASILAN_KOTOR * 0.01"),
                komponen(6, "POT_PPH21", EJenisGaji.POTONGAN, false, "0"),
                komponen(7, "POTONGAN", EJenisGaji.NONE, false, "POT_JP + POT_ASKES + POT_PPH21"),
                komponen(8, "PENGHASILAN_BERSIH", EJenisGaji.NONE, false, "PENGHASILAN_KOTOR - POTONGAN"),
                komponen(9, "PEMBULATAN", EJenisGaji.NONE, false, "( CEIL( PENGHASILAN_BERSIH / 100 ) * 100 ) - PENGHASILAN_BERSIH"),
                komponen(10, "PENGHASILAN_BERSIH_FINAL", EJenisGaji.NONE, false, "PENGHASILAN_BERSIH + PEMBULATAN"));

        GajiPreloadContext ctx = createContext(
                komponens,
                Map.of("maksimal_potongan_jpn", 100_423.0, "maksimal_potongan_askes", 120_000.0),
                Map.of(new GajiPreloadContext.JenisLevelKey(EJenisTunjangan.JABATAN, 5L), 1_234_567.0)
        );

        GajiBatchMaster master = master();
        HitungPegawaiResult result = service.hitung(master, ctx);

        assertTrue(result.isSuccess());
        assertNull(result.error());

        // GP 5.000.000 + TUNJ_JABATAN 1.234.567 → KOTOR 6.234.567
        // POT_JP 62.345,67 → round 62.346 (clamp 100.423 tak kena)
        // POT_ASKES 62.345,67 → round 62.346
        // POTONGAN 124.692; BERSIH 6.109.875; PEMBULATAN 25; FINAL 6.109.900
        assertEquals(6_234_567.0, master.getPenghasilanKotor());
        assertEquals(124_692.0, master.getTotalPotongan());
        assertEquals(6_109_875.0, master.getPenghasilanBersih());
        assertEquals(25.0, master.getPembulatan());
        assertEquals(6_109_900.0, master.getPenghasilanBersihFinal());
        assertEquals(0.0, master.getPajak());

        List<GajiBatchMasterProses> rows = result.prosesList();
        assertEquals(10, rows.size());
        // implicit resolve TUNJ_JABATAN → nilai lookup, bukan 0
        assertEquals(1_234_567.0, byKode(rows, "TUNJ_JABATAN").getNilai());
        assertEquals(62_346.0, byKode(rows, "POT_JP").getNilai());
        assertEquals(62_346.0, byKode(rows, "POT_ASKES").getNilai());
        assertEquals(25.0, byKode(rows, "PEMBULATAN").getNilai());
        // nilaiFormula = formula asli dgn token tersubstitusi (tanpa notasi ilmiah)
        assertEquals("5000000 + 1234567", byKode(rows, "PENGHASILAN_KOTOR").getNilaiFormula());
        assertEquals("GP + TUNJ_JABATAN", byKode(rows, "PENGHASILAN_KOTOR").getFormula());
        // token fungsi CEIL tidak tersubstitusi di nilaiFormula
        assertEquals("( CEIL( 6109875 / 100 ) * 100 ) - 6109875", byKode(rows, "PEMBULATAN").getNilaiFormula());
    }

    /** PHDP (formula kosong) di-resolve implisit dari snapshot (keputusan #11); TUNJ_SI tanpa lookup → 0.0. */
    @Test
    void hitung_formulaKosong_implicitResolvePerKode() {
        List<GajiKomponen> komponens = List.of(
                komponen(1, "PHDP", EJenisGaji.NONE, false, ""),
                komponen(2, "TUNJ_SI", EJenisGaji.PEMASUKAN, false, ""));

        GajiPreloadContext ctx = createContext(komponens, Map.of(), Map.of());

        GajiBatchMaster master = master();
        master.setPhdp(21_362_814.0);
        HitungPegawaiResult result = service.hitung(master, ctx);

        assertTrue(result.isSuccess());
        List<GajiBatchMasterProses> rows = result.prosesList();
        assertEquals(21_362_814.0, byKode(rows, "PHDP").getNilai());
        assertEquals(0.0, byKode(rows, "TUNJ_SI").getNilai());
    }

    /** W6-2: POT_JP melebihi cap → di-clamp ke maksimal_potongan_jpn. */
    @Test
    void hitung_clampPOT_JP() {
        List<GajiKomponen> komponens = List.of(
                komponen(1, "GP", EJenisGaji.PEMASUKAN, true, "#SYSTEM"),
                komponen(2, "TUNJ_JABATAN", EJenisGaji.PEMASUKAN, false, ""),
                komponen(3, "POT_JP", EJenisGaji.POTONGAN, false, "( GP + TUNJ_JABATAN ) * 0.01"));

        GajiPreloadContext ctx = createContext(
                komponens,
                Map.of("maksimal_potongan_jpn", 100_423.0),
                Map.of(new GajiPreloadContext.JenisLevelKey(EJenisTunjangan.JABATAN, 5L), 1_250_000.0)
        );

        GajiBatchMaster master = master();
        master.setGajiPokok(50_000_000.0);
        HitungPegawaiResult result = service.hitung(master, ctx);

        assertTrue(result.isSuccess());
        // 0,01 * 51.250.000 = 512.500 > cap 100.423 → 100.423
        assertEquals(100_423.0, byKode(result.prosesList(), "POT_JP").getNilai());
    }

    /** W6-2: parameter clamp tidak ditemukan → tanpa cap (bukan cap 0 seperti default legacy). */
    @Test
    void hitung_clampParamHilang_tanpaCap() {
        List<GajiKomponen> komponens = List.of(
                komponen(1, "GP", EJenisGaji.PEMASUKAN, true, "#SYSTEM"),
                komponen(2, "TUNJ_JABATAN", EJenisGaji.PEMASUKAN, false, ""),
                komponen(3, "POT_JP", EJenisGaji.POTONGAN, false, "( GP + TUNJ_JABATAN ) * 0.01"));

        GajiPreloadContext ctx = createContext(
                komponens,
                Map.of(),
                Map.of(new GajiPreloadContext.JenisLevelKey(EJenisTunjangan.JABATAN, 5L), 1_250_000.0)
        );

        GajiBatchMaster master = master();
        master.setGajiPokok(50_000_000.0);
        HitungPegawaiResult result = service.hitung(master, ctx);

        assertTrue(result.isSuccess());
        assertEquals(512_500.0, byKode(result.prosesList(), "POT_JP").getNilai());
    }

    /** isReference/#SYSTEM di-resolve via resolver; token REF_* di formula tersubstitusi dari ctx. */
    @Test
    void hitung_isReference_dan_tokenRefDiFormula() {
        List<GajiKomponen> komponens = List.of(
                komponen(1, "REF_PTKP", EJenisGaji.NONE, true, "#SYSTEM"),
                komponen(2, "POT_PPH21", EJenisGaji.POTONGAN, false, "REF_PTKP * 0.1"));

        GajiPreloadContext ctx = new GajiPreloadContext(
                Map.of(1L, komponens),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(10L, 54_000_000.0),
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                Set.of()
        );

        GajiBatchMaster master = master();
        master.setGajiPendapatanNonPajakId(new GajiPendapatanNonPajak(10L, "K0", 54_000_000.0, null));
        HitungPegawaiResult result = service.hitung(master, ctx);

        assertTrue(result.isSuccess());
        List<GajiBatchMasterProses> rows = result.prosesList();
        assertEquals(54_000_000.0, byKode(rows, "REF_PTKP").getNilai());
        assertEquals(5_400_000.0, byKode(rows, "POT_PPH21").getNilai());
        assertEquals("54000000 * 0.1", byKode(rows, "POT_PPH21").getNilaiFormula());
        assertEquals(5_400_000.0, master.getPajak());
    }

    @Test
    void hitung_komponenProfilKosong_returnError() {
        GajiPreloadContext ctx = createContext(List.of(), Map.of(), Map.of());
        HitungPegawaiResult result = service.hitung(master(), ctx);

        assertFalse(result.isSuccess());
        assertNotNull(result.error());
        assertTrue(result.error().notes().contains("Komponen profil"));
    }

    @Test
    void hitung_formulaError_returnErrorData() {
        List<GajiKomponen> komponens = List.of(
                komponen(1, "TEST", EJenisGaji.PEMASUKAN, false, "INVALID +++ FORMULA"));
        GajiPreloadContext ctx = createContext(komponens, Map.of(), Map.of());

        HitungPegawaiResult result = service.hitung(master(), ctx);

        assertFalse(result.isSuccess());
        assertNotNull(result.error());
    }
}