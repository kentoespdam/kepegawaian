package id.perumdamts.kepegawaian.services.cuti.kuota;

import id.perumdamts.kepegawaian.config.CutiProperties;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.helpers.DateHelper;
import id.perumdamts.kepegawaian.repositories.cuti.CutiKuotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaDeductionResult;
import id.perumdamts.kepegawaian.helpers.cuti.CutiKuotaDeductionAllocator;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CutiKuotaUpdateByCutiService {
    private final CutiKuotaRepository repository;
    private final CutiProperties cutiProperties;

    /**
     * Update the leave quota of the employee in the database.
     * This is called when the leave request is approved or rejected.
     * The logic for updating the quota is as follows:
     * 1. If the start year is greater than the current year and the end year is greater than the current year,
     * update the quota for the current year and the next year.
     * 2. If the start year is equal to the current year and the end year is greater than the current year,
     * update the quota for the current year and the next year.
     * 3. If the start date is after January 1st of the start year and before June 30th of the start year,
     * update the quota for the previous year and the current year.
     * 4. If the start date is after July 1st of the start year and before December 31st of the start year,
     * update the quota for the current year.
     * 5. If the start date is before June 30th of the start year and the end date is after July 1st of the start year,
     * update the quota for the previous year and the current year.
     *
     * @param cutiPegawai the leave request containing the employee ID, start and end dates, and other details.
     */
    public void updateKuota(CutiPegawai cutiPegawai) {
        int currentYear = LocalDate.now().getYear();
        LocalDate tanggalMulai = cutiPegawai.getTanggalMulai();
        LocalDate tanggalSelesai = cutiPegawai.getTanggalSelesai();
        int startYear = tanggalMulai.getYear();
        int endYear = tanggalSelesai.getYear();

        if (cutiPegawai.getJenisCuti().getId().equals(cutiProperties.getJenisCutiTahunan())) {
            if (startYear > currentYear && endYear > currentYear) {
                int previousYear = startYear - 1;
                doUpdateKuota(cutiPegawai, previousYear, endYear);
            } else if (startYear == currentYear && endYear > currentYear) {
                doUpdateKuota(cutiPegawai, startYear, endYear);
            } else if (DateHelper.isBetweenDates(tanggalMulai, tanggalSelesai, startYear, 1, 6, 30)) {
                int previousYear = startYear - 1;
                doUpdateKuota(cutiPegawai, previousYear, startYear);
            } else if (DateHelper.isBetweenDates(tanggalMulai, tanggalSelesai, startYear, 7, 12, 31)) {
                updateKuotaForYear(cutiPegawai, startYear, cutiPegawai.getRiwayatPakai0());
            } else if (DateHelper.isOverlappingDates(tanggalMulai, tanggalSelesai, startYear)) {
                int previousYear = startYear - 1;
                doUpdateKuota(cutiPegawai, previousYear, startYear);
            }
        } else {
            int previousYear = startYear - 1;
            doUpdateKuota(cutiPegawai, previousYear, startYear);
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
