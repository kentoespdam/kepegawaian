package id.perumdamts.kepegawaian.repositories.cuti;

import id.perumdamts.kepegawaian.entities.cuti.CutiApprovalChain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CutiApprovalChainRepository extends JpaRepository<CutiApprovalChain, Long> {
    List<CutiApprovalChain> findByRefCutiIdAndApprovalLevelGreaterThanEqualOrderByApprovalLevelAsc(Long refCutiId, Integer approvalLevel);

    List<CutiApprovalChain> findByRefCutiIdAndApprovalLevelLessThanEqualOrderByApprovalLevelDesc(Long id, Integer approvalLevel);
}
