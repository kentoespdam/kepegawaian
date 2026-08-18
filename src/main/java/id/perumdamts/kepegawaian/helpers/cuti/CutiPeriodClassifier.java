package id.perumdamts.kepegawaian.helpers.cuti;

import id.perumdamts.kepegawaian.entities.commons.ECutiPeriod;
import id.perumdamts.kepegawaian.helpers.DateHelper;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Klasifikasi periode cuti tahunan + derivasi pasangan tahun kuota (year0, year1).
 *
 * <p>Aturan main (kepegawaian-ebt / kepegawaian-ciw): klasifikasi & pemetaan
 * period→tahun kuota hanya boleh dianchori ke data yang <b>beku</b> (tanggal cuti +
 * tahun referensi saat pengajuan), <b>bukan</b> ke {@code LocalDate.now()} di fase
 * approval/settlement — kalau pakai jam, cuti yang sama bisa diklasifikasikan beda
 * (atau crash) tergantung kapan disetujui.</p>
 */
public final class CutiPeriodClassifier {
    private CutiPeriodClassifier() {}

    /** Pasangan tahun kuota yang disentuh sebuah periode: {@code year0} ← riwayatPakai0, {@code year1} ← riwayatPakai1. */
    public record YearPair(int year0, int year1) {}

    public static ECutiPeriod classify(LocalDate start, LocalDate end, int refYear) {
        int startYear = start.getYear();
        int endYear = end.getYear();

        // Cuti seluruhnya di tahun (atau tahun-tahun) setelah tahun referensi.
        if (startYear > refYear && endYear > refYear) {
            return ECutiPeriod.NEXT_YEAR;
        }
        // Menyeberang batas tahun (endYear > startYear) → OVERLAPPING, apa pun refYear-nya.
        // Sebelumnya syaratnya `startYear == refYear` — itu yang membuat cuti lintas-tahun
        // yang disetujui di tahun berikutnya jatuh ke no-branch (silent no-op / crash).
        if (endYear > startYear) {
            return ECutiPeriod.OVERLAPPING;
        }
        if (DateHelper.isBetweenDates(start, end, startYear, 1, 6, 30)) {
            return ECutiPeriod.JAN_JUN;
        }
        if (DateHelper.isBetweenDates(start, end, startYear, 7, 12, 31)) {
            return ECutiPeriod.JUL_DES;
        }
        return ECutiPeriod.JUN_JUL;
    }

    /**
     * Tahun referensi untuk fase settlement: tahun pengajuan (= {@code createdAt}),
     * beku per baris. Fallback defensif bila {@code createdAt} null (data legacy):
     * {@code startYear - 1} — deterministik, tidak pernah crash.
     */
    public static int resolveRefYear(LocalDateTime createdAt, LocalDate start) {
        return createdAt != null ? createdAt.getYear() : start.getYear() - 1;
    }

    /**
     * Klasifikasi deterministik untuk fase approval/settlement: anchor ke
     * {@code createdAt} (bukan {@code now()}), jadi hasilnya tidak bergantung pada
     * kapan approval terjadi.
     */
    public static ECutiPeriod resolvePeriod(LocalDate start, LocalDate end, LocalDateTime createdAt) {
        return classify(start, end, resolveRefYear(createdAt, start));
    }

    /**
     * Memetakan periode → pasangan tahun kuota yang disentuh. Satu-satunya sumber
     * kebenaran untuk pemotongan kuota; dipakai bersama oleh handler submission dan
     * settlement (kepegawaian-ebt: dulu setiap tempat menghitung tahunnya sendiri).
     *
     * <ul>
     *   <li>{@code NEXT_YEAR} → (refYear, endYear) — sisa tahun berjalan dulu, lalu tahun cuti.
     *       Dulu memakai {@code startYear - 1} yang hanya benar bila {@code startYear == refYear + 1}.</li>
     *   <li>{@code OVERLAPPING} → (startYear, endYear)</li>
     *   <li>{@code JAN_JUN} → (startYear - 1, startYear)</li>
     *   <li>{@code JUL_DES} → (startYear, startYear)</li>
     *   <li>{@code JUN_JUL} → (startYear - 1, startYear)</li>
     * </ul>
     */
    public static YearPair deriveYearPair(ECutiPeriod period, LocalDate start, LocalDate end, int refYear) {
        return switch (period) {
            case NEXT_YEAR -> new YearPair(refYear, end.getYear());
            case OVERLAPPING -> new YearPair(start.getYear(), end.getYear());
            case JAN_JUN, JUN_JUL -> new YearPair(start.getYear() - 1, start.getYear());
            case JUL_DES -> new YearPair(start.getYear(), start.getYear());
        };
    }
}
