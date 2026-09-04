package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses;

import id.perumdamts.kepegawaian.entities.commons.EJenisGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMasterProses;
import id.perumdamts.kepegawaian.entities.penggajian.GajiKomponen;
import id.perumdamts.kepegawaian.entities.penggajian.GajiParameterSetting;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchMasterProsesRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchMasterRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiKomponenRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiParameterSettingRepository;
import id.perumdamts.kepegawaian.utils.GajiFormulaEvaluator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GajiBatchProsesKalkulasiServiceTest {

    private static final String BATCH_ID = "batch-1";

    @Mock GajiKomponenRepository gajiKomponenRepository;
    @Mock GajiBatchMasterProsesRepository gajiBatchMasterProsesRepository;
    @Mock GajiBatchMasterRepository gajiBatchMasterRepository;
    @Mock GajiParameterSettingRepository gajiParameterSettingRepository;
    @Mock GajiBatchProsesReferenceResolver referenceResolver;

    @Spy
    GajiFormulaEvaluator formulaEvaluator = new GajiFormulaEvaluator();

    @InjectMocks
    GajiBatchProsesKalkulasiService service;

    private GajiKomponen komponen(int urut, String kode, EJenisGaji jenis, boolean isRef, String formula) {
        return new GajiKomponen(urut, null, kode, kode, jenis, 0, isRef, formula);
    }

    private GajiBatchMaster master() {
        GajiBatchMaster m = new GajiBatchMaster();
        m.setId(1L);
        m.setGajiProfilId(1L);
        m.setNipam("12345");
        return m;
    }

    private void stubCtxSeeds() {
        when(referenceResolver.resolve(eq("JML_ANAK"), any(), any(), any())).thenReturn(2.0);
        when(referenceResolver.resolve(eq("JML_JIWA"), any(), any(), any())).thenReturn(4.0);
    }

    private List<GajiBatchMasterProses> capturedProses() {
        ArgumentCaptor<List<GajiBatchMasterProses>> captor = ArgumentCaptor.forClass(List.class);
        verify(gajiBatchMasterProsesRepository).saveAll(captor.capture());
        return captor.getValue();
    }

    private GajiBatchMasterProses byKode(List<GajiBatchMasterProses> rows, String kode) {
        return rows.stream().filter(r -> r.getKode().equals(kode)).findFirst().orElseThrow();
    }

    /** Alur penuh profil 1: implicit resolve TUNJ_JABATAN, evaluasi formula, clamp, round, total master. */
    @Test
    void hitung_alurPenuh_profil1() {
        stubCtxSeeds();
        when(referenceResolver.resolve(eq("GP"), any(), any(), any())).thenReturn(5_000_000.0);
        when(referenceResolver.resolve(eq("REF_TUNJ_JABATAN"), any(), any(), any())).thenReturn(1_234_567.0);
        when(gajiParameterSettingRepository.findByKode("maksimal_potongan_jpn"))
                .thenReturn(Optional.of(new GajiParameterSetting(1L, "maksimal_potongan_jpn", 100_423.0)));
        when(gajiParameterSettingRepository.findByKode("maksimal_potongan_askes"))
                .thenReturn(Optional.of(new GajiParameterSetting(2L, "maksimal_potongan_askes", 120_000.0)));

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
        when(gajiKomponenRepository.findByProfilGajiIdOrderByUrutAsc(1L)).thenReturn(komponens);

        GajiBatchMaster master = master();
        service.hitung(master, BATCH_ID);

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
        verify(gajiBatchMasterRepository).save(master);

        List<GajiBatchMasterProses> rows = capturedProses();
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
        stubCtxSeeds();
        when(referenceResolver.resolve(eq("REF_PHDP"), any(), any(), any())).thenReturn(21_362_814.0);

        List<GajiKomponen> komponens = List.of(
                komponen(1, "PHDP", EJenisGaji.NONE, false, ""),
                komponen(2, "TUNJ_SI", EJenisGaji.PEMASUKAN, false, ""));
        when(gajiKomponenRepository.findByProfilGajiIdOrderByUrutAsc(1L)).thenReturn(komponens);

        GajiBatchMaster master = master();
        service.hitung(master, BATCH_ID);

        List<GajiBatchMasterProses> rows = capturedProses();
        assertEquals(21_362_814.0, byKode(rows, "PHDP").getNilai());
        assertEquals(0.0, byKode(rows, "TUNJ_SI").getNilai());
        // kode tanpa lookup tidak boleh menyentuh resolver
        verify(referenceResolver, never()).resolve(eq("REF_TUNJ_SI"), any(), any(), any());
        verify(referenceResolver, never()).resolve(eq("TUNJ_SI"), any(), any(), any());
    }

    /** W6-2: POT_JP melebihi cap → di-clamp ke maksimal_potongan_jpn. */
    @Test
    void hitung_clampPOT_JP() {
        stubCtxSeeds();
        when(referenceResolver.resolve(eq("GP"), any(), any(), any())).thenReturn(50_000_000.0);
        when(referenceResolver.resolve(eq("REF_TUNJ_JABATAN"), any(), any(), any())).thenReturn(1_250_000.0);
        when(gajiParameterSettingRepository.findByKode("maksimal_potongan_jpn"))
                .thenReturn(Optional.of(new GajiParameterSetting(1L, "maksimal_potongan_jpn", 100_423.0)));

        List<GajiKomponen> komponens = List.of(
                komponen(1, "GP", EJenisGaji.PEMASUKAN, true, "#SYSTEM"),
                komponen(2, "TUNJ_JABATAN", EJenisGaji.PEMASUKAN, false, ""),
                komponen(3, "POT_JP", EJenisGaji.POTONGAN, false, "( GP + TUNJ_JABATAN ) * 0.01"));
        when(gajiKomponenRepository.findByProfilGajiIdOrderByUrutAsc(1L)).thenReturn(komponens);

        service.hitung(master(), BATCH_ID);

        // 0,01 * 51.250.000 = 512.500 > cap 100.423 → 100.423
        assertEquals(100_423.0, byKode(capturedProses(), "POT_JP").getNilai());
    }

    /** W6-2: parameter clamp tidak ditemukan → tanpa cap (bukan cap 0 seperti default legacy). */
    @Test
    void hitung_clampParamHilang_tanpaCap() {
        stubCtxSeeds();
        when(referenceResolver.resolve(eq("GP"), any(), any(), any())).thenReturn(50_000_000.0);
        when(referenceResolver.resolve(eq("REF_TUNJ_JABATAN"), any(), any(), any())).thenReturn(1_250_000.0);
        when(gajiParameterSettingRepository.findByKode("maksimal_potongan_jpn"))
                .thenReturn(Optional.empty());

        List<GajiKomponen> komponens = List.of(
                komponen(1, "GP", EJenisGaji.PEMASUKAN, true, "#SYSTEM"),
                komponen(2, "TUNJ_JABATAN", EJenisGaji.PEMASUKAN, false, ""),
                komponen(3, "POT_JP", EJenisGaji.POTONGAN, false, "( GP + TUNJ_JABATAN ) * 0.01"));
        when(gajiKomponenRepository.findByProfilGajiIdOrderByUrutAsc(1L)).thenReturn(komponens);

        service.hitung(master(), BATCH_ID);

        assertEquals(512_500.0, byKode(capturedProses(), "POT_JP").getNilai());
    }

    /** isReference/#SYSTEM di-resolve via resolver; token REF_* di formula tersubstitusi dari ctx. */
    @Test
    void hitung_isReference_dan_tokenRefDiFormula() {
        stubCtxSeeds();
        when(referenceResolver.resolve(eq("REF_PTKP"), any(), any(), any())).thenReturn(54_000_000.0);

        List<GajiKomponen> komponens = List.of(
                komponen(1, "REF_PTKP", EJenisGaji.NONE, true, "#SYSTEM"),
                komponen(2, "POT_PPH21", EJenisGaji.POTONGAN, false, "REF_PTKP * 0.1"));
        when(gajiKomponenRepository.findByProfilGajiIdOrderByUrutAsc(1L)).thenReturn(komponens);

        service.hitung(master(), BATCH_ID);

        List<GajiBatchMasterProses> rows = capturedProses();
        assertEquals(54_000_000.0, byKode(rows, "REF_PTKP").getNilai());
        assertEquals(5_400_000.0, byKode(rows, "POT_PPH21").getNilai());
        assertEquals("54000000 * 0.1", byKode(rows, "POT_PPH21").getNilaiFormula());
        // POT_PPH21 → pajak master
        assertEquals(5_400_000.0, masterFromSave().getPajak());
    }

    private GajiBatchMaster masterFromSave() {
        ArgumentCaptor<GajiBatchMaster> captor = ArgumentCaptor.forClass(GajiBatchMaster.class);
        verify(gajiBatchMasterRepository).save(captor.capture());
        return captor.getValue();
    }
}