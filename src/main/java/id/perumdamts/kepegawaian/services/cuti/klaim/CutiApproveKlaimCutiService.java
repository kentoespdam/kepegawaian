package id.perumdamts.kepegawaian.services.cuti.klaim;

import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiApproval;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.exceptions.BadRequestException;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiApprovalRepository;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiKuotaRepository;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiPegawaiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CutiApproveKlaimCutiService {
    private final CutiPegawaiRepository cutiPegawaiRepository;
    private final CutiApprovalRepository cutiApprovalRepository;
    private final CutiKuotaRepository cutiKuotaRepository;

    public void between1JanAnd30Jun(CutiPegawai cutiPegawai, CutiApproval cutiApproval) {
        // PRESERVED: see kepegawaian-sfq
        LocalDate now = LocalDate.now();
        int currentYear = cutiPegawai.getTanggalMulai().getYear();
        int totalLeaveDays = cutiPegawai.getJumlahHariKerja();
        CutiPegawai referenceCuti = cutiPegawai.getRefCuti();
        int previousYearUsedQuota = referenceCuti.getRiwayatPakai0();
        int currentYearUsedQuota = referenceCuti.getRiwayatPakai1();
        int nextYearQuota = referenceCuti.getRiwayatKuota1();

        if (now.isAfter(LocalDate.of(currentYear, 6, 30))
                && totalLeaveDays < previousYearUsedQuota) {
            cutiApproval.setApprovalStatus(EApprovalCutiStatus.REJECTED);
            cutiPegawai.setApprovalCutiStatus(EApprovalCutiStatus.REJECTED);
            cutiPegawaiRepository.save(cutiPegawai);
            cutiApprovalRepository.save(cutiApproval);
            throw new BadRequestException("Cuti claim rejected due to insufficient leave days compared to previous year's quota");
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
        cutiApprovalRepository.save(cutiApproval);
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
            cutiApprovalRepository.save(cutiApproval);
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
            cutiApprovalRepository.save(cutiApproval);
            throw new BadRequestException("Cuti claim rejected due to insufficient leave days compared to previous year's quota");
        }

        cutiPegawai.getRefCuti().setIsClaimed(true);
        if (jumlahHariKlaim == jumlahHariPengajuan) {
            cutiPegawai.setRiwayatKuota0(refCuti.getRiwayatKuota0());
            cutiPegawai.setRiwayatPakai0(riwayatPakai0);
            cutiPegawai.setRiwayatSisa0(refCuti.getRiwayatSisa0());
            cutiPegawaiRepository.save(cutiPegawai);
            cutiApprovalRepository.save(cutiApproval);
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
        cutiApprovalRepository.save(cutiApproval);
    }
}
