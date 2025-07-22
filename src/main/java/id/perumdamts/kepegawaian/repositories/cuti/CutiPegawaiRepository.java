package id.perumdamts.kepegawaian.repositories.cuti;

import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CutiPegawaiRepository extends JpaRepository<CutiPegawai, Long>,
        JpaSpecificationExecutor<CutiPegawai>,
        RevisionRepository<CutiPegawai, Long, Long> {
    Optional<CutiPegawai> findByIdAndApprovalCutiStatus(Long id, EApprovalCutiStatus eApprovalCutiStatus);

    boolean existsByPegawai_IdAndJenisCuti_IdAndApprovalCutiStatusIn(@NotNull(message = "Pegawai is required") @Min(value = 1, message = "Pegawai is required") Long pegawaiId, @NotNull(message = "Jenis Cuti is required") @Min(value = 1, message = "Jenis Cuti is required") Long jenisCutiId, List<EApprovalCutiStatus> approvalStatus);

    Optional<CutiPegawai> findByIdAndApprovalCutiStatusIn(@NotNull(message = "Cuti is required") @Min(value = 1, message = "Cuti is required") Long cutiId, List<EApprovalCutiStatus> eApprovalCutiStatus);

    @Query(value = "SELECT cp.* " +
                    "FROM cuti_pegawai AS cp " +
                    "INNER JOIN ( SELECT * FROM cuti_approval_chain WHERE jabatan_id = :picSaatIniId AND skip = 0 LIMIT 1 ) AS cac ON cp.id = cac.ref_cuti_id " +
                    "AND cp.approval_level <= cac.approval_level " +
                    "WHERE " +
                    "cp.is_deleted = 0 " +
                    "AND ( cp.created_at = :tahun OR cp.tanggal_mulai = :tahun ) " +
                    "AND cp.pic_saat_ini_id = :picSaatIniId " +
                    "OR cp.approval_cuti_status = :approvalCutiStatus ",
            countQuery = "SELECT COUNT(*) " +
                    "FROM cuti_pegawai AS cp " +
                    "INNER JOIN ( SELECT * FROM cuti_approval_chain WHERE jabatan_id = :picSaatIniId AND skip = 0 LIMIT 1 ) AS cac ON cp.id = cac.ref_cuti_id " +
                    "AND cp.approval_level <= cac.approval_level " +
                    "WHERE " +
                    "cp.is_deleted = 0 " +
                    "AND ( cp.created_at = :tahun OR cp.tanggal_mulai = :tahun ) " +
                    "AND cp.pic_saat_ini_id = :picSaatIniId " +
                    "OR cp.approval_cuti_status = :approvalCutiStatus ",
            nativeQuery = true
    )
    Page<CutiPegawai> findForApproval(@Param("tahun") int tahun, @Param("picSaatIniId") Long picSaatIniId, @Param("approvalCutiStatus") int approvalCutiStatus, Pageable pageable);
}
