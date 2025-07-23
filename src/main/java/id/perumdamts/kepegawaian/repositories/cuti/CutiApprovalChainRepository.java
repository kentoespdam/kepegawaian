package id.perumdamts.kepegawaian.repositories.cuti;

import id.perumdamts.kepegawaian.entities.commons.EApprovalCutiStatus;
import id.perumdamts.kepegawaian.entities.cuti.CutiApprovalChain;
import id.perumdamts.kepegawaian.entities.cuti.CutiPegawai;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CutiApprovalChainRepository extends JpaRepository<CutiApprovalChain, Long>,
        JpaSpecificationExecutor<CutiApprovalChain>,
        CutiApprovalChainCustomRepository {
    List<CutiApprovalChain> findByRefCuti_IdOrderByApprovalLevelAsc(Long refCutiId);

    List<CutiApprovalChain> findByRefCuti_IdAndSkipOrderByApprovalLevelAsc(Long cutiPegawaiId, boolean skip);

    List<CutiApprovalChain> findByRefCuti_IdAndApprovalLevelLessThanEqualOrderByApprovalLevelDesc(Long refCutiId, Integer approvalLevel);

    List<CutiApprovalChain> findByRefCuti_IdAndApprovalLevelLessThanEqualAndSkipOrderByApprovalLevelDesc(Long refCutiId, Integer approvalLevel, boolean skip);

    @Query("SELECT cac.refCuti FROM CutiApprovalChain cac " +
            "WHERE cac.refCuti.id = :refCutiId " +
            "AND cac.jabatanId = :jabatanId " +
            "AND (YEAR(cac.refCuti.createdAt) = :tahun OR YEAR(cac.refCuti.tanggalMulai) = :tahun) " +
            "AND cac.refCuti.approvalCutiStatus = :approvalCutiStatus " +
            "GROUP BY cac.refCuti.id"
    )
    Page<CutiPegawai> findCutiApprovalPegawai(@Param("tahun") Integer tahun, @Param("refCutiId") Long refCutiId, @Param("jabatanId") Long jabatanId, @Param("approvalCutiStatus") EApprovalCutiStatus approvalCutiStatus, Pageable pageable);
}
