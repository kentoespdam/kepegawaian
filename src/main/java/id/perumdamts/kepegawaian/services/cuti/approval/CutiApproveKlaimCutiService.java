package id.perumdamts.kepegawaian.services.cuti.approval;

import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.repositories.cuti.CutiKuotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CutiApproveKlaimCutiService {
    private final CutiKuotaRepository cutiKuotaRepository;

    public CutiPegawai forNextYear(CutiPegawai cutiPegawai) {
        int currentYear = cutiPegawai.getTanggalMulai().getYear() - 1;
        int nextYear = cutiPegawai.getTanggalSelesai().getYear();
        return this.separateCutiWithNextYear(cutiPegawai, currentYear, nextYear, cutiPegawai.getPegawai().getId());
    }

    private CutiPegawai separateCutiWithNextYear(CutiPegawai cutiPegawai, int currentYear, int nextYear, Long pegawaiId) {
        if (cutiPegawai.getJumlahHariKerja().equals(cutiPegawai.getRefCuti().getJumlahHariKerja())) {
            cutiPegawai.setRiwayatPakai0(cutiPegawai.getRefCuti().getRiwayatPakai0());
            cutiPegawai.setRiwayatPakai1(cutiPegawai.getRefCuti().getRiwayatPakai1());
            return cutiPegawai;
        }

        return cutiPegawai;
    }
}
