package id.perumdamts.kepegawaian.repositories.cuti;

import id.perumdamts.kepegawaian.entities.cuti.CutiApprovalChain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CutiApprovalChainRepository extends JpaRepository<CutiApprovalChain, Long>, JpaSpecificationExecutor<CutiApprovalChain> {
    List<CutiApprovalChain> findByRefCuti_IdOrderByApprovalLevelAsc(Long refCutiId);

    List<CutiApprovalChain> findByRefCuti_IdAndSkipOrderByApprovalLevelAsc(Long cutiPegawaiId, boolean skip);

    List<CutiApprovalChain> findByRefCuti_IdAndApprovalLevelLessThanEqualOrderByApprovalLevelDesc(Long refCutiId, Integer approvalLevel);

    List<CutiApprovalChain> findByRefCuti_IdAndApprovalLevelLessThanEqualAndSkipOrderByApprovalLevelDesc(Long refCutiId, Integer approvalLevel, boolean skip);
}
