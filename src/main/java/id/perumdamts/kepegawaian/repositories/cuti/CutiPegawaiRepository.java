package id.perumdamts.kepegawaian.repositories.cuti;

import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CutiPegawaiRepository extends JpaRepository<CutiPegawai, Long>,
        JpaSpecificationExecutor<CutiPegawai>,
        RevisionRepository<CutiPegawai, Long, Integer> {
    Optional<CutiPegawai> findByIdAndApprovalCutiStatus(Long id, EApprovalCutiStatus eApprovalCutiStatus);

    boolean existsByPegawai_IdAndJenisCuti_IdAndApprovalCutiStatusIn(@NotNull(message = "Pegawai is required") @Min(value = 1, message = "Pegawai is required") Long pegawaiId, @NotNull(message = "Jenis Cuti is required") @Min(value = 1, message = "Jenis Cuti is required") Long jenisCutiId, List<EApprovalCutiStatus> approvalStatus);

    Optional<CutiPegawai> findByIdAndApprovalCutiStatusIn(@NotNull(message = "Cuti is required") @Min(value = 1, message = "Cuti is required") Long cutiId, List<EApprovalCutiStatus> eApprovalCutiStatus);

    @Query("select count(c) > 0 from CutiPegawai c where c.pegawai.id = :pegawaiId and year(c.tanggalMulai) = :year and c.approvalCutiStatus = :status")
    boolean existsPending(@Param("pegawaiId") Long pegawaiId, @Param("year") int year, @Param("status") EApprovalCutiStatus status);

    @Query("select count(c) > 0 from CutiPegawai c where c.pegawai.id = :pegawaiId and c.jenisCuti.id = :jenisCutiId and year(c.tanggalMulai) = :year and c.approvalCutiStatus in :statuses")
    boolean existsByJenisCutiAndYear(@Param("pegawaiId") Long pegawaiId, @Param("jenisCutiId") Long jenisCutiId, @Param("year") int year, @Param("statuses") List<EApprovalCutiStatus> statuses);
}
