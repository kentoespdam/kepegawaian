package id.perumdamts.kepegawaian.services.cuti.klaim;

import id.perumdamts.kepegawaian.config.CutiProperties;
import id.perumdamts.kepegawaian.dto.cuti.pengajuan.CutiPengajuanKlaimPostRequest;
import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import id.perumdamts.kepegawaian.repositories.cuti.jpa.CutiPegawaiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CutiKlaimValidator {
    private final CutiPegawaiRepository repository;
    private final CutiProperties cutiProperties;

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
