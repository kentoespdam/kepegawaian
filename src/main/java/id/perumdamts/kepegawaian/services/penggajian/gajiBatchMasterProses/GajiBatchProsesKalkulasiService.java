package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses;

import id.perumdamts.kepegawaian.entities.commons.EJenisErrorGaji;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMasterProses;
import id.perumdamts.kepegawaian.entities.penggajian.GajiKomponen;
import id.perumdamts.kepegawaian.exceptions.GajiFormulaException;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses.preload.ErrorEntry;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses.preload.GajiPreloadContext;
import id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses.preload.HitungPegawaiResult;
import id.perumdamts.kepegawaian.utils.GajiFormulaEvaluator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Engine kalkulasi gaji (Wave 6) — zero-DB, zero-transaction virtual thread safe.
 *
 * <p>Per pegawai: ambil {@link GajiKomponen} profil dari {@link GajiPreloadContext},
 * akumulasi nilai di {@code ctxMap}, evaluasi formula via {@link GajiFormulaEvaluator} (exp4j),
 * dan kembalikan {@link HitungPegawaiResult} murni di memori (tanpa DB write).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GajiBatchProsesKalkulasiService {

    /** Kode komponen ber-formula kosong yang legacy tetap bayar via lookup → kode resolver. */
    private static final Map<String, String> KODE_EMPTY_IMPLICIT = Map.of(
            "TUNJ_JABATAN", "REF_TUNJ_JABATAN",
            "TUNJ_BERAS", "REF_TUNJ_BERAS",
            "TUNJ_KK", "REF_TUNJ_KK",
            "TUNJ_AIR", "REF_TUNJ_AIR",
            "PHDP", "REF_PHDP");

    /** Token formula non-komponen yang di-seed dari snapshot master (audit legacy). */
    private static final String[] CTX_SEED = {"JML_ANAK", "JML_JIWA"};

    /** Token formula (kode komponen / referensi) — regex boundary aman utk REF_TUNJ_KK vs TUNJ_KK. */
    private static final Pattern TOKEN = Pattern.compile("\\b[A-Z][A-Z0-9_]*\\b");

    private final GajiFormulaEvaluator formulaEvaluator;

    /**
     * Hitung seluruh komponen pegawai di memori dan kembalikan hasilnya.
     * Tidak melakukan DB write (zero-DB) sehingga aman dijalankan di virtual threads.
     *
     * @param master snapshot pegawai (Wave 5) — totalnya di-update di memori
     * @param ctx    preloaded context penggajian
     * @return HitungPegawaiResult berisi master, prosesList, dan error jika ada
     */
    public HitungPegawaiResult hitung(GajiBatchMaster master, GajiPreloadContext ctx) {
        if (master == null) {
            return new HitungPegawaiResult(null, List.of(), new ErrorEntry(
                    null, null, EJenisErrorGaji.SYSTEM, "Master pegawai bernilai null"));
        }

        try {
            List<GajiKomponen> komponens = ctx.getKomponenByProfilId(master.getGajiProfilId());
            if (komponens == null || komponens.isEmpty()) {
                return new HitungPegawaiResult(master, List.of(), new ErrorEntry(
                        master.getNipam(), master.getNama(), EJenisErrorGaji.DATA,
                        "Komponen profil gaji tidak ditemukan"));
            }

            Map<String, Double> ctxMap = new HashMap<>();
            for (String kode : CTX_SEED) {
                ctxMap.put(kode, ctx.resolve(kode, master, ctxMap));
            }

            List<GajiBatchMasterProses> prosesList = new ArrayList<>();
            for (GajiKomponen k : komponens) {
                double nilai = hitungKomponen(k, master, ctxMap, ctx);
                double bulat = Math.round(ctx.clampPotongan(k.getKode(), nilai));
                ctxMap.put(k.getKode(), bulat);
                prosesList.add(new GajiBatchMasterProses(
                        null, master.getId(), k.getKode(), k.getUrut(), k.getNama(),
                        k.getJenisGaji(), bulat, k.getFormula(),
                        substitusiFormula(k.getFormula(), ctxMap)));
            }

            updateTotal(master, ctxMap);
            return new HitungPegawaiResult(master, prosesList, null);
        } catch (GajiFormulaException e) {
            log.warn("DATA error pegawai {}: {}", master.getNipam(), e.getMessage());
            return new HitungPegawaiResult(master, List.of(), new ErrorEntry(
                    master.getNipam(), master.getNama(), EJenisErrorGaji.DATA, e.getMessage()));
        } catch (Exception e) {
            log.error("SYSTEM error pegawai {}: {}", master.getNipam(), e.getMessage(), e);
            return new HitungPegawaiResult(master, List.of(), new ErrorEntry(
                    master.getNipam(), master.getNama(), EJenisErrorGaji.SYSTEM, e.getMessage()));
        }
    }

    /** Resolve satu komponen: isReference/#SYSTEM → resolver; formula kosong → implicit resolve; else evaluator. */
    private double hitungKomponen(GajiKomponen k, GajiBatchMaster master, Map<String, Double> ctxMap, GajiPreloadContext ctx) {
        if (Boolean.TRUE.equals(k.getIsReference()) || "#SYSTEM".equals(k.getFormula()))
            return ctx.resolve(k.getKode(), master, ctxMap);
        if (k.getFormula() == null || k.getFormula().isBlank()) {
            String refKode = KODE_EMPTY_IMPLICIT.get(k.getKode());
            if (refKode != null)
                return ctx.resolve(refKode, master, ctxMap);
            return 0.0;
        }
        return formulaEvaluator.evaluate(k.getFormula(), ctxMap);
    }

    /** nilaiFormula = formula asli dgn token komponen diganti nilai (round) dari ctx — token tanpa ctx dibiarkan. */
    private String substitusiFormula(String formula, Map<String, Double> ctx) {
        if (formula == null || formula.isBlank())
            return formula;
        Matcher m = TOKEN.matcher(formula);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            Double v = ctx.get(m.group());
            if (v != null)
                m.appendReplacement(sb, Matcher.quoteReplacement(
                        BigDecimal.valueOf(v).stripTrailingZeros().toPlainString()));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /** Total master dari ctx — legacy: {@code tax} = nilai komponen POT_PPH21 (p_pph21). */
    private void updateTotal(GajiBatchMaster master, Map<String, Double> ctx) {
        master.setPenghasilanKotor(ctx.get("PENGHASILAN_KOTOR"));
        master.setTotalPotongan(ctx.get("POTONGAN"));
        master.setPenghasilanBersih(ctx.get("PENGHASILAN_BERSIH"));
        master.setPembulatan(ctx.get("PEMBULATAN"));
        master.setPenghasilanBersihFinal(ctx.get("PENGHASILAN_BERSIH_FINAL"));
        master.setPajak(ctx.get("POT_PPH21"));
    }
}