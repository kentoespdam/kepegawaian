package id.perumdamts.kepegawaian.repositories.cuti;

import id.perumdamts.kepegawaian.entities.cuti.CutiApprovalChain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CutiApprovalChainRepository extends JpaRepository<CutiApprovalChain, Long>, JpaSpecificationExecutor<CutiApprovalChain> {
    List<CutiApprovalChain> findByRefCutiIdAndApprovalLevelGreaterThanEqualOrderByApprovalLevelAsc(Long refCutiId, Integer approvalLevel);

    List<CutiApprovalChain> findByRefCutiIdAndApprovalLevelLessThanEqualOrderByApprovalLevelDesc(Long id, Integer approvalLevel);
}
