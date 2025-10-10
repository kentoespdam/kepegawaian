package id.perumdamts.kepegawaian.repositories.cuti;

import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.List;
import java.util.Optional;

public interface CutiPegawaiRepository extends JpaRepository<CutiPegawai, Long>,
        JpaSpecificationExecutor<CutiPegawai>,
        RevisionRepository<CutiPegawai, Long, Integer> {
    Optional<CutiPegawai> findByIdAndApprovalCutiStatus(Long id, EApprovalCutiStatus eApprovalCutiStatus);

    boolean existsByPegawai_IdAndJenisCuti_IdAndApprovalCutiStatusIn(@NotNull(message = "Pegawai is required") @Min(value = 1, message = "Pegawai is required") Long pegawaiId, @NotNull(message = "Jenis Cuti is required") @Min(value = 1, message = "Jenis Cuti is required") Long jenisCutiId, List<EApprovalCutiStatus> approvalStatus);

    Optional<CutiPegawai> findByIdAndApprovalCutiStatusIn(@NotNull(message = "Cuti is required") @Min(value = 1, message = "Cuti is required") Long cutiId, List<EApprovalCutiStatus> eApprovalCutiStatus);
}
