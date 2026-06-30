package id.perumdamts.kepegawaian.services.cuti.pengajuan;

import id.perumdamts.kepegawaian.config.CutiProperties;
import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaSisa;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanKlaimPostRequest;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.helpers.cuti.MinimalCutiRule;
import id.perumdamts.kepegawaian.repositories.cuti.CutiJenisRepository;
import id.perumdamts.kepegawaian.repositories.cuti.CutiPegawaiRepository;
import id.perumdamts.kepegawaian.repositories.cuti.jooq.CutiKuotaQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CutiPengajuanValidator {
    private final CutiPegawaiRepository repository;
    private final CutiJenisRepository cutiJenisRepository;
    private final CutiKuotaQueryRepository cutiKuotaQueryRepository;
    private final CutiProperties cutiProperties;

    public void validate(CutiPengajuanPostRequest request) {
        // Check if the employee has any pending leave request
        boolean existsPendingPengajuan = repository.existsPending(
                request.getPegawaiId(),
                request.getTanggalMulai().getYear(),
                EApprovalCutiStatus.PENDING
        );
        if (existsPendingPengajuan) {
            throw new RuntimeException("Masih ada pengajuan cuti yang belum diapprove");
        }

        // Check if the employee has taken a long leave
        boolean existBesar = repository.existsByJenisCutiAndYear(
                request.getPegawaiId(),
                cutiProperties.getJenisCutiBesar(),
                request.getTanggalMulai().getYear(),
                List.of(
                        EApprovalCutiStatus.PENDING,
                        EApprovalCutiStatus.APPROVED,
                        EApprovalCutiStatus.CONFIRMED,
                        EApprovalCutiStatus.RETURNED
                )
        );
        if (existBesar) {
            throw new RuntimeException("Anda tidak berhak cuti tahunan karena telah mengambil cuti besar");
        }

        // Check if the employee has taken a leave for performing a religious obligation
        boolean existIbadah = repository.existsByPegawai_IdAndJenisCuti_IdAndApprovalCutiStatusIn(
                request.getPegawaiId(),
                cutiProperties.getJenisCutiIbadah(),
                List.of(
                        EApprovalCutiStatus.PENDING,
                        EApprovalCutiStatus.APPROVED,
                        EApprovalCutiStatus.CONFIRMED,
                        EApprovalCutiStatus.RETURNED
                )
        );
        if (existIbadah) {
            throw new RuntimeException("Anda tidak berhak cuti tahunan karena telah mengambil cuti melaksanakan ibadah");
        }

        // Validate remaining quota & minimal cuti
        CutiJenis cutiJenis = cutiJenisRepository.findById(request.getJenisCutiId())
                .orElseThrow(() -> new RuntimeException("Unknown Jenis Cuti"));

        CutiKuotaSisa kuotaSisa = cutiKuotaQueryRepository.findByPegawai(request.getPegawaiId(), request.getTanggalMulai().getYear());
        int totalSisaKuota = kuotaSisa.getSisaCutiTahunIni() + kuotaSisa.getSisaCutiTahunLalu();

        if (Boolean.TRUE.equals(cutiJenis.getPotongKuotaTahunan())) {
            // Minimal cuti check (Fase 2)
            MinimalCutiRule.check(request.getJumlahHariKerja(), totalSisaKuota);

            // Validasi sisa kuota
            if (request.getJumlahHariKerja() > totalSisaKuota) {
                throw new RuntimeException("Kuota cuti tidak mencukupi");
            }
        }
    }

    public CutiPegawai validateKlaim(CutiPengajuanKlaimPostRequest request) {
        // Cek apakah referensi cuti ini sudah disetujui
        CutiPegawai cutiPegawai = repository.findByIdAndApprovalCutiStatus(
                request.getRefCutiId(), EApprovalCutiStatus.APPROVED
        ).orElseThrow(() -> new RuntimeException("Unknown Cuti Pegawai"));

        if (!List.of(cutiProperties.getJenisCutiTahunan(), cutiProperties.getJenisCutiIbadah()).contains(cutiPegawai.getJenisCuti().getId()))
            throw new RuntimeException("Cuti ini tidak perlu di klaim");

        // Cek apakah pengajuan klaim cuti ini sudah ada
        boolean exists = repository.existsByPegawai_IdAndJenisCuti_IdAndApprovalCutiStatusIn(
                request.getPegawaiId(),
                cutiPegawai.getJenisCuti().getId(),
                List.of(
                        EApprovalCutiStatus.PENDING,
                        EApprovalCutiStatus.APPROVED,
                        EApprovalCutiStatus.CONFIRMED,
                        EApprovalCutiStatus.RETURNED
                )
        );
        if (exists) {
            throw new RuntimeException("Pengajuan Klaim Cuti ini sudah ada");
        }

        // Cek apakah ada cuti melaksanakan ibadah yang masih berlangsung atau belum disetujui
        boolean existCutiIbadah = repository.existsByPegawai_IdAndJenisCuti_IdAndApprovalCutiStatusIn(
                request.getPegawaiId(),
                cutiProperties.getJenisCutiIbadah(),
                List.of(EApprovalCutiStatus.PENDING, EApprovalCutiStatus.RETURNED)
        );
        if (existCutiIbadah) {
            throw new RuntimeException("Klaim cuti tidak dapat diproses karena masih ada pengajuan cuti melaksanakan ibadah yang masih berlangsung");
        }

        return cutiPegawai;
    }
}
