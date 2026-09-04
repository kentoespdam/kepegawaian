package id.perumdamts.kepegawaian.services.penggajian.gajiBatchMasterProses;

import id.perumdamts.kepegawaian.entities.commons.EJenisTunjangan;
import id.perumdamts.kepegawaian.entities.commons.EStatusKawin;
import id.perumdamts.kepegawaian.entities.kepegawaian.RiwayatSp;
import id.perumdamts.kepegawaian.entities.penggajian.GajiBatchMaster;
import id.perumdamts.kepegawaian.entities.penggajian.GajiKpi;
import id.perumdamts.kepegawaian.entities.penggajian.GajiPotonganTkk;
import id.perumdamts.kepegawaian.entities.penggajian.GajiTunjangan;
import id.perumdamts.kepegawaian.repositories.kepegawaian.jpa.RiwayatSpRepository;
import id.perumdamts.kepegawaian.repositories.pegawai.jpa.PegawaiRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.GajiBatchPotonganTkkRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiKpiRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiPotonganTkkRepository;
import id.perumdamts.kepegawaian.repositories.penggajian.jpa.GajiTunjanganRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import java.util.Optional;

/**
 * Resolver komponen referensi (#SYSTEM) engine proses gaji — lihat tabel
 * "#SYSTEM Resolver Map" di docs/penggajian-proses-gaji-claim-order.md (Wave 4-1).
 *
 * Semua lookup kembali ke snapshot {@link GajiBatchMaster} atau master data
 * (level/golongan/status pegawai di-snapshot saat Wave 5). {@code REF_ASKES} dan
 * {@code REF_SEWA_RUMDIN} sengaja di-resolve live dari {@code Pegawai} (keputusan
 * user 2026-09-04 — kolomnya tidak ada di snapshot {@code gaji_batch_master}).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GajiBatchProsesReferenceResolver {

    private final GajiTunjanganRepository gajiTunjanganRepository;
    private final GajiPotonganTkkRepository gajiPotonganTkkRepository;
    private final GajiBatchPotonganTkkRepository gajiBatchPotonganTkkRepository;
    private final GajiKpiRepository gajiKpiRepository;
    private final RiwayatSpRepository riwayatSpRepository;
    private final PegawaiRepository pegawaiRepository;

    /**
     * Resolve satu kode komponen referensi menjadi nilai double.
     *
     * @param kode    kode komponen (mis. {@code REF_PTKP})
     * @param master  snapshot pegawai (Wave 5)
     * @param ctx     akumulator nilai komponen yang sudah dihitung (Wave 6)
     * @param batchId id batch root (untuk lookup potongan TKK per batch)
     */
    public double resolve(String kode, GajiBatchMaster master, Map<String, Double> ctx, String batchId) {
        return switch (kode) {
            case "GP" -> nvl(master.getGajiPokok());
            case "JML_ANAK" -> nvl(master.getJmlTanggungan());
            case "JML_JIWA" -> jmlJiwa(master);
            case "REF_PTKP" -> refPtkp(master);
            case "REF_ASKES" -> refAskes(master);
            case "REF_SEWA_RUMDIN" -> refSewaRumdin(master);
            case "REF_POT_TKK" -> refPotTkk(master);
            case "REF_JML_POT_KK", "REF_JML_POT_TKK" -> refJmlPotTkk(master, batchId);
            case "REF_TUNJ_JABATAN" -> refTunjangan(master, EJenisTunjangan.JABATAN);
            case "REF_TUNJ_BERAS" -> refTunjangan(master, EJenisTunjangan.BERAS);
            case "REF_TUNJ_KK" -> refTunjangan(master, EJenisTunjangan.KINERJA);
            case "REF_TUNJ_AIR" -> refTunjangan(master, EJenisTunjangan.AIR);
            case "REF_PHDP" -> nvl(master.getPhdp());
            case "TUNJ_KINERJA" -> tunjKinerja(master);
            default -> {
                // Kode tak dikenal bukan SYSTEM error — komponen seed bermasalah (DATA error).
                // Perilaku sama dgn legacy: switch default tidak mengisi value (0.0).
                log.warn("Kode reference tidak dikenal di resolver: '{}' (pegawai {})", kode, master.getNipam());
                yield 0.0;
            }
        };
    }

    /** GP / JML_ANAK / REF_PHDP — langsung dari snapshot master. */
    private double nvl(Double v) {
        return v != null ? v : 0.0;
    }

    private double nvl(Integer v) {
        return v != null ? v : 0.0;
    }

    /**
     * JML_JIWA = 1 + JML_ANAK + (KAWIN/MENIKAH_SEKANTOR ? 1 : 0) — lihat
     * resolver map (computed dari {@code GajiBatchMaster.statusKawin}).
     */
    private double jmlJiwa(GajiBatchMaster master) {
        EStatusKawin kawin = master.getStatusKawin();
        boolean menikah = kawin == EStatusKawin.KAWIN || kawin == EStatusKawin.MENIKAH_SEKANTOR;
        return 1 + nvl(master.getJmlTanggungan()) + (menikah ? 1 : 0);
    }

    /** REF_PTKP — nominal GajiPendapatanNonPajak (relasi snapshot di master). */
    private double refPtkp(GajiBatchMaster master) {
        if (master.getGajiPendapatanNonPajakId() == null)
            return 0.0;
        return nvl(master.getGajiPendapatanNonPajakId().getNominal());
    }

    /** REF_ASKES — {@code Pegawai.isAskes ? 1.0 : 0.0} (live lookup, keputusan user). */
    private double refAskes(GajiBatchMaster master) {
        if (master.getPegawaiId() == null)
            return 0.0;
        return pegawaiRepository.findIsAskesById(master.getPegawaiId())
                .map(Boolean::booleanValue)
                .map(b -> b ? 1.0 : 0.0)
                .orElse(0.0);
    }

    /** REF_SEWA_RUMDIN — {@code Pegawai.rumahDinas.nilai}, 0.0 jika rumahDinas null. */
    private double refSewaRumdin(GajiBatchMaster master) {
        if (master.getPegawaiId() == null)
            return 0.0;
        return pegawaiRepository.findRumahDinasNilaiById(master.getPegawaiId()).orElse(0.0);
    }

    /**
     * REF_POT_TKK — {@code GajiPotonganTkk} by statusPegawai + levelId/golonganId,
     * fallback ke baris flat per statusPegawai (KONTRAK/CAPEG/CALON_HONORER/HONORER).
     * Level-keyed dulu, lalu golongan-keyed — pola seed V20 (level 2-6 vs level 7).
     */
    private double refPotTkk(GajiBatchMaster master) {
        Optional<GajiPotonganTkk> hit = Optional.empty();
        if (master.getLevelId() != null)
            hit = gajiPotonganTkkRepository.findByStatusPegawaiAndLevelIdAndGolonganIsNull(
                    master.getStatusPegawai(), master.getLevelId());
        if (hit.isEmpty() && master.getGolonganId() != null)
            hit = gajiPotonganTkkRepository.findByStatusPegawaiAndGolonganId(
                    master.getStatusPegawai(), master.getGolonganId());
        if (hit.isEmpty())
            hit = gajiPotonganTkkRepository.findByStatusPegawaiAndLevelIsNullAndGolonganIsNull(
                    master.getStatusPegawai());
        return hit.map(GajiPotonganTkk::getNominal).orElse(0.0);
    }

    /** REF_JML_POT_KK — SUM(GajiBatchPotonganTkk.potongan) by batchId + nipam; default 0. */
    private double refJmlPotTkk(GajiBatchMaster master, String batchId) {
        Long sum = gajiBatchPotonganTkkRepository.sumPotonganByBatchIdAndNipam(batchId, master.getNipam());
        return sum != null ? sum.doubleValue() : 0.0;
    }

    /**
     * REF_TUNJ_* — {@code GajiTunjangan} lookup. Level-keyed dulu (golongan null),
     * lalu golongan-keyed — pola seed (level 5/6 vs level 7 + golongan).
     * REF_TUNJ_KK (KINERJA) → 0 jika SP-3 aktif di periode gaji (keputusan #10).
     */
    private double refTunjangan(GajiBatchMaster master, EJenisTunjangan jenis) {
        if (jenis == EJenisTunjangan.KINERJA && sp3Aktif(master))
            return 0.0;

        Optional<GajiTunjangan> hit = Optional.empty();
        if (master.getLevelId() != null)
            hit = gajiTunjanganRepository.findByJenisTunjanganAndLevelIdAndGolonganIsNull(jenis, master.getLevelId());
        if (hit.isEmpty() && master.getGolonganId() != null)
            hit = gajiTunjanganRepository.findByJenisTunjanganAndGolonganId(jenis, master.getGolonganId());
        return hit.map(GajiTunjangan::getNominal).orElse(0.0);
    }

    /**
     * SP-3 aktif di periode {@code master.periode} ("YYYYMM" atau "YYYY-MM"):
     * interval {@code RiwayatSp} [tanggalMulai, tanggalSelesai] overlap window
     * gaji [prev-month-21, current-month-20] — pola legacy emp_notice.notice_start_date.
     */
    private boolean sp3Aktif(GajiBatchMaster master) {
        if (master.getPegawaiId() == null || master.getPeriode() == null)
            return false;
        try {
            YearMonth ym = YearMonth.parse(normalizePeriode(master.getPeriode()));
            LocalDate windowEnd = ym.atDay(20);
            LocalDate windowStart = ym.minusMonths(1).atDay(21);
            return riwayatSpRepository.existsSp3Aktif(master.getPegawaiId(), windowEnd, windowStart);
        } catch (Exception e) {
            log.warn("Periode tidak valid utk SP-3 check: '{}' (pegawai {})", master.getPeriode(), master.getNipam());
            return false;
        }
    }

    /** TUNJ_KINERJA — {@code GajiKpi.tunkin} by nipam + periode (GajiKpi pakai format YYYY-MM); default 0. */
    private double tunjKinerja(GajiBatchMaster master) {
        return gajiKpiRepository.findByNipamAndPeriode(master.getNipam(), normalizePeriode(master.getPeriode()))
                .map(GajiKpi::getTunkin)
                .map(v -> v != null ? v : 0.0)
                .orElse(0.0);
    }

    /** "202509" → "2025-09"; sudah "YYYY-MM" → apa adanya. */
    private String normalizePeriode(String periode) {
        if (periode == null || periode.length() != 6 || !periode.chars().allMatch(Character::isDigit))
            return periode;
        return periode.substring(0, 4) + "-" + periode.substring(4);
    }
}