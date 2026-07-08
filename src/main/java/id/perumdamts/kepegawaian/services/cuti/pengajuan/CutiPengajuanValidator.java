package id.perumdamts.kepegawaian.services.cuti.pengajuan;

import id.perumdamts.kepegawaian.config.CutiProperties;
import id.perumdamts.kepegawaian.dto.cuti.kuota.CutiKuotaSisa;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiJenis;
import id.perumdamts.kepegawaian.helpers.cuti.MinimalCutiRule;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiJenisRepository;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiPegawaiRepository;
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
        boolean existsPendingPengajuan = repository.existsPending(
                request.getPegawaiId(), request.getTanggalMulai().getYear(), EApprovalCutiStatus.PENDING
        );
        if (existsPendingPengajuan) {
            throw new RuntimeException("Masih ada pengajuan cuti yang belum diapprove");
        }

        List<EApprovalCutiStatus> activeStatuses = List.of(
                EApprovalCutiStatus.PENDING, EApprovalCutiStatus.APPROVED,
                EApprovalCutiStatus.CONFIRMED, EApprovalCutiStatus.RETURNED
        );

        boolean existBesar = repository.existsByJenisCutiAndYear(
                request.getPegawaiId(), cutiProperties.getJenisCutiBesar(),
                request.getTanggalMulai().getYear(), activeStatuses
        );
        if (existBesar) {
            throw new RuntimeException("Anda tidak berhak cuti tahunan karena telah mengambil cuti besar");
        }

        boolean existIbadah = repository.existsByPegawai_IdAndJenisCuti_IdAndApprovalCutiStatusIn(
                request.getPegawaiId(), cutiProperties.getJenisCutiIbadah(), activeStatuses
        );
        if (existIbadah) {
            throw new RuntimeException("Anda tidak berhak cuti tahunan karena telah mengambil cuti melaksanakan ibadah");
        }

        CutiJenis cutiJenis = cutiJenisRepository.findById(request.getJenisCutiId())
                .orElseThrow(() -> new RuntimeException("Unknown Jenis Cuti"));

        CutiKuotaSisa kuotaSisa = cutiKuotaQueryRepository.findByPegawai(request.getPegawaiId(), request.getTanggalMulai().getYear());
        int totalSisaKuota = kuotaSisa.sisaCutiTahunIni() + kuotaSisa.sisaCutiTahunLalu();

        if (Boolean.TRUE.equals(cutiJenis.getPotongKuotaTahunan())) {
            MinimalCutiRule.check(request.getJumlahHariKerja(), totalSisaKuota);
            if (request.getJumlahHariKerja() > totalSisaKuota) {
                throw new RuntimeException("Kuota cuti tidak mencukupi");
            }
        }
    }
}
