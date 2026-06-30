package id.perumdamts.kepegawaian.services.cuti.klaim;

import id.perumdamts.kepegawaian.entities.cuti.CutiApproval;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.repositories.cuti.CutiApprovalRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiKuotaRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiPegawaiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CutiKlaimCrossYearSettlement {
    private final CutiPegawaiRepository cutiPegawaiRepository;
    private final CutiApprovalRepository cutiApprovalRepository;
    private final CutiKuotaRepository cutiKuotaRepository;

    public void forNextYear(CutiPegawai cutiPegawai, CutiApproval cutiApproval) {
        // PRESERVED: see kepegawaian-ciw
        int currentYear = cutiPegawai.getTanggalMulai().getYear() - 1;
        int nextYear = cutiPegawai.getTanggalSelesai().getYear();
        this.separateCutiWithNextYear(cutiPegawai, currentYear, nextYear, cutiPegawai.getPegawai().getId());
        cutiApprovalRepository.save(cutiApproval);
    }

    public void overlappingYear(CutiPegawai cutiPegawai, CutiApproval cutiApproval) {
        int currentYear = cutiPegawai.getTanggalMulai().getYear();
        int nextYear = cutiPegawai.getTanggalSelesai().getYear();

        this.separateCutiWithNextYear(cutiPegawai, currentYear, nextYear, cutiPegawai.getPegawai().getId());
        cutiApprovalRepository.save(cutiApproval);
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
