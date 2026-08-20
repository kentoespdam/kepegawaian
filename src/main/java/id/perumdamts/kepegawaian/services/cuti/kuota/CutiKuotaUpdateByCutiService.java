package id.perumdamts.kepegawaian.services.cuti.kuota;

import id.perumdamts.kepegawaian.config.CutiProperties;
import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaDeductionResult;
import id.perumdamts.kepegawaian.entities.commons.ECutiPeriod;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.helpers.cuti.CutiKuotaDeductionAllocator;
import id.perumdamts.kepegawaian.helpers.cuti.CutiPeriodClassifier;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiKuotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CutiKuotaUpdateByCutiService {
    private final CutiKuotaRepository repository;
    private final CutiProperties cutiProperties;

    /**
     * Update the leave quota of the employee in the database.
     * This is called when the leave request is approved (final).
     *
     * <p>kepegawaian-ebt: klasifikasi & pemetaan period→tahun di-anchor ke
     * {@code createdAt} (tahun pengajuan) — BUKAN {@code LocalDate.now()} — jadi cuti
     * yang sama memotong kuota yang sama persis, tidak peduli kapan approval terjadi.
     * Dulu salinan inline 5-cabang ini re-klasifikasi dengan now(), sehingga cuti
     * OVERLAPPING yang disetujui di tahun berikutnya jatuh ke no-branch (silent no-op).</p>
     *
     * @param cutiPegawai the leave request containing the employee ID, start and end dates, and other details.
     */
    public void updateKuota(CutiPegawai cutiPegawai) {
        LocalDate tanggalMulai = cutiPegawai.getTanggalMulai();
        LocalDate tanggalSelesai = cutiPegawai.getTanggalSelesai();
        int startYear = tanggalMulai.getYear();

        if (cutiPegawai.getJenisCuti().getId().equals(cutiProperties.jenisCutiTahunan())) {
            ECutiPeriod period = CutiPeriodClassifier.resolvePeriod(tanggalMulai, tanggalSelesai, cutiPegawai.getCreatedAt());
            CutiPeriodClassifier.YearPair pair = CutiPeriodClassifier.deriveYearPair(
                    period, tanggalMulai, tanggalSelesai,
                    CutiPeriodClassifier.resolveRefYear(cutiPegawai.getCreatedAt(), tanggalMulai));
            switch (period) {
                case JUL_DES -> updateKuotaForYear(cutiPegawai, pair.year0(), cutiPegawai.getRiwayatPakai0());
                case NEXT_YEAR, OVERLAPPING, JAN_JUN, JUN_JUL ->
                        doUpdateKuota(cutiPegawai, pair.year0(), pair.year1());
            }
        } else {
            doUpdateKuota(cutiPegawai, startYear - 1, startYear);
        }
    }

    /**
     * Update the leave quota for the given year.
     * This is called by the updateKuota method.
     * If the quota for the given year is not found in the database,
     * it will be created. Otherwise, the existing quota will be updated.
     *
     * @param cutiPegawai the leave request containing the employee ID, start and end dates, and other details.
     * @param year        the year of the quota to be updated.
     * @param pakai       the number of leaves taken by the employee in the given year.
     */
    private void updateKuotaForYear(CutiPegawai cutiPegawai, int year, int pakai) {
        repository.findByPegawai_IdAndTahun(cutiPegawai.getPegawai().getId(), year)
                .ifPresent(kuota -> {
                    CutiKuotaDeductionResult res = CutiKuotaDeductionAllocator.deduct(
                            kuota.getKuotaTerpakai(),
                            kuota.getSisaKuota(),
                            pakai
                    );
                    kuota.setKuotaTerpakai(res.getNewKuotaTerpakai());
                    kuota.setSisaKuota(res.getNewSisaKuota());
                    repository.save(kuota);
                });
    }

    /**
     * Update the leave quota for the given years.
     * This is called by the updateKuota method.
     * If the quota for the given years is not found in the database,
     * it will be created. Otherwise, the existing quota will be updated.
     *
     * @param cutiPegawai the leave request containing the employee ID, start and end dates, and other details.
     * @param year0       the first year of the quota to be updated.
     * @param year1       the second year of the quota to be updated.
     */
    private void doUpdateKuota(CutiPegawai cutiPegawai, int year0, int year1) {
        updateKuotaForYear(cutiPegawai, year0, cutiPegawai.getRiwayatPakai0());
        updateKuotaForYear(cutiPegawai, year1, cutiPegawai.getRiwayatPakai1());
    }

}
