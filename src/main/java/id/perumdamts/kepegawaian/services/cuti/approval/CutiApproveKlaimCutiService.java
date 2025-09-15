package id.perumdamts.kepegawaian.services.cuti.approval;

import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiApproval;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.repositories.cuti.CutiApprovalRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiKuotaRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiPegawaiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CutiApproveKlaimCutiService {
    private final CutiApprovalRepository repository;
    private final CutiKuotaRepository cutiKuotaRepository;
    private final CutiPegawaiRepository cutiPegawaiRepository;

    public void forNextYear(CutiPegawai cutiPegawai, CutiApproval cutiApproval) {
        int currentYear = cutiPegawai.getTanggalMulai().getYear() - 1;
        int nextYear = cutiPegawai.getTanggalSelesai().getYear();
        this.separateCutiWithNextYear(cutiPegawai, currentYear, nextYear, cutiPegawai.getPegawai().getId());
        repository.save(cutiApproval);
    }

    public void overlappingYear(CutiPegawai cutiPegawai, CutiApproval cutiApproval) {
        int currentYear = cutiPegawai.getTanggalMulai().getYear();
        int nextYear = cutiPegawai.getTanggalSelesai().getYear();

        this.separateCutiWithNextYear(cutiPegawai, currentYear, nextYear, cutiPegawai.getPegawai().getId());
        repository.save(cutiApproval);
    }

    /**
     * Approve cuti when its date is between 1 Jan and 30 Jun.
     * If the approval date is after 30 Jun, then the cuti will be rejected if the number of days is less than the remaining cuti quota of the previous year.
     * Otherwise, the cuti will be approved and the remaining cuti quota of the previous year will be updated.
     *
     * @param cutiPegawai  the cuti pegawai entity.
     * @param cutiApproval the cuti approval entity.
     * @throws RuntimeException if the cuti is rejected.
     */
    public void between1JanAnd30Jun(CutiPegawai cutiPegawai, CutiApproval cutiApproval) {
        LocalDate now = LocalDate.now();
        int currentYear = cutiPegawai.getTanggalMulai().getYear();
        int totalLeaveDays = cutiPegawai.getJumlahHariKerja();
        CutiPegawai referenceCuti = cutiPegawai.getRefCuti();
        int previousYearUsedQuota = referenceCuti.getRiwayatPakai0();
        int currentYearUsedQuota = referenceCuti.getRiwayatPakai1();
        int nextYearQuota = referenceCuti.getRiwayatKuota1();

        // Check if the approval date is after 30 June
        if (now.isAfter(LocalDate.of(currentYear, 6, 30))
                && totalLeaveDays < previousYearUsedQuota) {
            // Reject if total leave days are less than previous year's used quota
            cutiApproval.setApprovalStatus(EApprovalCutiStatus.REJECTED);
            cutiPegawai.setApprovalCutiStatus(EApprovalCutiStatus.REJECTED);
            cutiPegawaiRepository.save(cutiPegawai);
            repository.save(cutiApproval);
            throw new RuntimeException("Cuti claim rejected due to insufficient leave days compared to previous year's quota");
        }

        int remainingCurrentYearQuota = totalLeaveDays - previousYearUsedQuota;
        cutiPegawai.setRiwayatKuota0(referenceCuti.getRiwayatKuota0());
        cutiPegawai.setRiwayatPakai0(previousYearUsedQuota);
        cutiPegawai.setRiwayatSisa0(referenceCuti.getRiwayatSisa0());
        cutiPegawai.setRiwayatKuota1(nextYearQuota);
        cutiPegawai.setRiwayatPakai1(currentYearUsedQuota + remainingCurrentYearQuota);
        cutiPegawai.setRiwayatSisa1(nextYearQuota - (currentYearUsedQuota + remainingCurrentYearQuota));
        cutiPegawai.getRefCuti().setIsClaimed(true);

        cutiKuotaRepository.findByPegawai_IdAndTahun(cutiPegawai.getPegawai().getId(), currentYear).ifPresent(cutiKuota -> {
            cutiKuota.setKuotaTerpakai(cutiKuota.getKuotaTerpakai() - currentYearUsedQuota + remainingCurrentYearQuota);
            cutiKuota.setSisaKuota(nextYearQuota - (currentYearUsedQuota + remainingCurrentYearQuota));
            cutiKuotaRepository.save(cutiKuota);
        });

        cutiPegawaiRepository.save(cutiPegawai);
        repository.save(cutiApproval);
    }

    public void between1JulAnd31Dec(CutiPegawai cutiPegawai, CutiApproval cutiApproval) {
        CutiPegawai refCuti = cutiPegawai.getRefCuti();
        int jumlahHariKlaim = cutiPegawai.getJumlahHariKerja();
        int jumlahHariPengajuan = refCuti.getJumlahHariKerja();
        int riwayatPakai0 = refCuti.getRiwayatPakai0();

        if (jumlahHariKlaim == jumlahHariPengajuan) {
            cutiPegawai.setRiwayatKuota0(refCuti.getRiwayatKuota0());
            cutiPegawai.setRiwayatPakai0(riwayatPakai0);
            cutiPegawai.setRiwayatSisa0(refCuti.getRiwayatSisa0());
            cutiPegawai.getRefCuti().setIsClaimed(true);
            cutiPegawaiRepository.save(cutiPegawai);
            repository.save(cutiApproval);
            return;
        }

        int sisa = jumlahHariPengajuan - jumlahHariKlaim;
        cutiPegawai.setRiwayatKuota0(refCuti.getRiwayatKuota0());
        cutiPegawai.setRiwayatPakai0(jumlahHariKlaim);
        cutiPegawai.setRiwayatSisa0(refCuti.getRiwayatSisa0() + sisa);
        cutiPegawai.getRefCuti().setIsClaimed(true);
        updateKuotaCuti(cutiPegawai, cutiApproval, sisa);

    }

    public void between30JunAnd1Jul(CutiPegawai cutiPegawai, CutiApproval cutiApproval) {
        CutiPegawai refCuti = cutiPegawai.getRefCuti();
        int jumlahHariKlaim = cutiPegawai.getJumlahHariKerja();
        int jumlahHariPengajuan = refCuti.getJumlahHariKerja();
        int riwayatPakai0 = refCuti.getRiwayatPakai0();

        if (jumlahHariKlaim < riwayatPakai0) {
            cutiApproval.setApprovalStatus(EApprovalCutiStatus.REJECTED);
            cutiPegawai.setApprovalCutiStatus(EApprovalCutiStatus.REJECTED);
            cutiPegawaiRepository.save(cutiPegawai);
            repository.save(cutiApproval);
            throw new RuntimeException("Cuti claim rejected due to insufficient leave days compared to previous year's quota");
        }

        cutiPegawai.getRefCuti().setIsClaimed(true);
        if (jumlahHariKlaim == jumlahHariPengajuan) {
            cutiPegawai.setRiwayatKuota0(refCuti.getRiwayatKuota0());
            cutiPegawai.setRiwayatPakai0(riwayatPakai0);
            cutiPegawai.setRiwayatSisa0(refCuti.getRiwayatSisa0());
            cutiPegawaiRepository.save(cutiPegawai);
            repository.save(cutiApproval);
            return;
        }

        int sisa = jumlahHariPengajuan - jumlahHariKlaim;
        cutiPegawai.setRiwayatKuota0(refCuti.getRiwayatKuota0());
        cutiPegawai.setRiwayatPakai0(jumlahHariKlaim);
        cutiPegawai.setRiwayatSisa0(refCuti.getRiwayatSisa0() + sisa);
        updateKuotaCuti(cutiPegawai, cutiApproval, sisa);
    }

    private void updateKuotaCuti(CutiPegawai cutiPegawai, CutiApproval cutiApproval, int sisa) {
        cutiKuotaRepository.findByPegawai_IdAndTahun(cutiPegawai.getPegawai().getId(), cutiPegawai.getTanggalMulai().getYear())
                .ifPresent(kuota -> {
                    kuota.setKuotaTerpakai(kuota.getKuotaTerpakai() - sisa);
                    kuota.setSisaKuota(kuota.getSisaKuota() + sisa);
                    cutiKuotaRepository.save(kuota);
                });
        cutiPegawaiRepository.save(cutiPegawai);
        repository.save(cutiApproval);
    }

    private void separateCutiWithNextYear(CutiPegawai cutiPegawai, int currentYear, int nextYear, Long pegawaiId) {
        CutiPegawai refCuti = cutiPegawai.getRefCuti();
        cutiPegawai.getRefCuti().setIsClaimed(true);
        if (cutiPegawai.getJumlahHariKerja().equals(cutiPegawai.getRefCuti().getJumlahHariKerja())) {
            cutiPegawai.setRiwayatKuota0(refCuti.getRiwayatKuota0());
            cutiPegawai.setRiwayatPakai0(refCuti.getRiwayatPakai0());
            cutiPegawai.setRiwayatSisa0(refCuti.getRiwayatSisa0());
            cutiPegawai.setRiwayatKuota1(refCuti.getRiwayatKuota1());
            cutiPegawai.setRiwayatPakai1(refCuti.getRiwayatPakai1());
            cutiPegawai.setRiwayatSisa1(refCuti.getRiwayatSisa1());
            cutiPegawaiRepository.save(cutiPegawai);
            return;
        }

        Integer currentYearRemaining = refCuti.getRiwayatKuota0();
        Integer nextYearRemaining = refCuti.getRiwayatKuota1();
        Integer riwayatPakai0 = refCuti.getRiwayatPakai0();
        Integer riwayatPakai1 = refCuti.getRiwayatPakai1();
        Integer totalDays = cutiPegawai.getJumlahHariKerja();

        int remainingAfterCurrentYear = totalDays - currentYearRemaining;
        if (remainingAfterCurrentYear > 0) {
            if (nextYearRemaining < remainingAfterCurrentYear) {
                throw new RuntimeException("Kuota Cuti Tahun depan tidak tersedia! sisa kuota: " + nextYearRemaining + " hari");
            }

            cutiPegawai.setRiwayatKuota0(currentYearRemaining);
            cutiPegawai.setRiwayatPakai0(currentYearRemaining);
            cutiPegawai.setRiwayatSisa0(0);
            cutiKuotaRepository.findByPegawai_IdAndTahun(pegawaiId, currentYear).ifPresent(cutiKuota -> {
                cutiKuota.setKuotaTerpakai(cutiKuota.getKuotaTerpakai() - riwayatPakai0 + currentYearRemaining);
                cutiKuota.setSisaKuota(0);
                cutiKuotaRepository.save(cutiKuota);
            });

            cutiPegawai.setRiwayatKuota1(nextYearRemaining);
            cutiPegawai.setRiwayatPakai1(remainingAfterCurrentYear);
            cutiPegawai.setRiwayatSisa1(nextYearRemaining - remainingAfterCurrentYear);
            cutiKuotaRepository.findByPegawai_IdAndTahun(pegawaiId, nextYear).ifPresent(cutiKuota -> {
                cutiKuota.setKuotaTerpakai(cutiKuota.getKuotaTerpakai() - riwayatPakai1 + remainingAfterCurrentYear);
                cutiKuota.setSisaKuota(nextYearRemaining - remainingAfterCurrentYear);
                cutiKuotaRepository.save(cutiKuota);
            });
        } else {
            cutiPegawai.setRiwayatKuota0(currentYearRemaining);
            cutiPegawai.setRiwayatPakai0(totalDays);
            cutiPegawai.setRiwayatSisa0(currentYearRemaining - totalDays);
            cutiKuotaRepository.findByPegawai_IdAndTahun(pegawaiId, currentYear).ifPresent(cutiKuota -> {
                cutiKuota.setKuotaTerpakai(cutiKuota.getKuotaTerpakai() - riwayatPakai0 + totalDays);
                cutiKuota.setSisaKuota(currentYearRemaining - totalDays);
                cutiKuotaRepository.save(cutiKuota);
            });
        }

        cutiPegawaiRepository.save(cutiPegawai);
    }
}
