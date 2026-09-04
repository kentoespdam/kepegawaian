package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses;

import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMasterProses;
import id.perumdamts.kepegawaian.entities.penggajian.GajiKomponen;
import id.perumdamts.kepegawaian.entities.penggajian.GajiParameterSetting;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchMasterProsesRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiBatchMasterRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiKomponenRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiParameterSettingRepository;
import id.perumdamts.kepegawaian.utils.GajiFormulaEvaluator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Engine kalkulasi gaji (Wave 6) — lihat docs/penggajian-proses-gaji-claim-order.md.
 *
 * <p>Per pegawai: load {@link GajiKomponen} profil by {@code urut}, akumulasi nilai
 * di {@code ctx}, evaluasi formula via {@link GajiFormulaEvaluator} (exp4j), simpan
 * {@link GajiBatchMasterProses}, lalu update total {@link GajiBatchMaster}.
 *
 * <p>Keputusan user 2026-09-04 (implicit resolve per kode): komponen ber-formula kosong
 * yang punya lookup (profil 1/6/9: {@code TUNJ_JABATAN/BERAS/KK/AIR}, {@code PHDP}) tetap
 * di-resolve via resolver seperti legacy; kode tanpa lookup ({@code TUNJ_SI/ANAK/KESEHATAN/
 * PPH21}, {@code ASTEK}, {@code PKP}, {@code POT_PENSIUN}) → 0.0 (sama dgn guard legacy
 * {@code if ($formula != '')}).
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

    private final GajiKomponenRepository gajiKomponenRepository;
    private final GajiBatchMasterProsesRepository gajiBatchMasterProsesRepository;
    private final GajiBatchMasterRepository gajiBatchMasterRepository;
    private final GajiParameterSettingRepository gajiParameterSettingRepository;
    private final GajiBatchProsesReferenceResolver referenceResolver;
    private final GajiFormulaEvaluator formulaEvaluator;

    /**
     * Hitung seluruh komponen pegawai dan simpan hasilnya.
     *
     * @param master  snapshot pegawai (Wave 5) — totalnya di-update di sini
     * @param batchId id batch root (utk lookup potongan TKK per batch di resolver)
     */
    @Transactional
    public void hitung(GajiBatchMaster master, String batchId) {
        List<GajiKomponen> komponens = gajiKomponenRepository
                .findByProfilGajiIdOrderByUrutAsc(master.getGajiProfilId());

        Map<String, Double> ctx = new HashMap<>();
        for (String kode : CTX_SEED)
            ctx.put(kode, referenceResolver.resolve(kode, master, ctx, batchId));

        List<GajiBatchMasterProses> prosesList = new ArrayList<>();
        for (GajiKomponen k : komponens) {
            double nilai = hitungKomponen(k, master, ctx, batchId);
            double bulat = Math.round(clampPotongan(k.getKode(), nilai));
            ctx.put(k.getKode(), bulat);
            prosesList.add(new GajiBatchMasterProses(
                    null, master.getId(), k.getKode(), k.getUrut(), k.getNama(),
                    k.getJenisGaji(), bulat, k.getFormula(),
                    substitusiFormula(k.getFormula(), ctx)));
        }
        gajiBatchMasterProsesRepository.saveAll(prosesList);
        updateTotal(master, ctx);
        gajiBatchMasterRepository.save(master);
    }

    /** Resolve satu komponen: isReference/#SYSTEM → resolver; formula kosong → implicit resolve; else evaluator. */
    private double hitungKomponen(GajiKomponen k, GajiBatchMaster master, Map<String, Double> ctx, String batchId) {
        if (Boolean.TRUE.equals(k.getIsReference()) || "#SYSTEM".equals(k.getFormula()))
            return referenceResolver.resolve(k.getKode(), master, ctx, batchId);
        if (k.getFormula() == null || k.getFormula().isBlank()) {
            String refKode = KODE_EMPTY_IMPLICIT.get(k.getKode());
            if (refKode != null)
                return referenceResolver.resolve(refKode, master, ctx, batchId);
            return 0.0;
        }
        return formulaEvaluator.evaluate(k.getFormula(), ctx);
    }

    /**
     * W6-2 (keputusan #15, locked): clamp pasca-eval POT_JP/POT_ASKES ke
     * {@code gaji_parameter_setting}. Parameter tak ditemukan → tanpa cap + warn
     * (sengaja tidak meniru default legacy yang cap-nya 0 = semua ke-0).
     */
    private double clampPotongan(String kode, double nilai) {
        String paramKode = switch (kode) {
            case "POT_JP" -> "maksimal_potongan_jpn";
            case "POT_ASKES" -> "maksimal_potongan_askes";
            default -> null;
        };
        if (paramKode == null)
            return nilai;
        Optional<Double> cap = gajiParameterSettingRepository.findByKode(paramKode)
                .map(GajiParameterSetting::getNominal);
        if (cap.isEmpty()) {
            log.warn("Parameter clamp '{}' tidak ditemukan — {} tanpa cap", paramKode, kode);
            return nilai;
        }
        return Math.min(nilai, cap.get());
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