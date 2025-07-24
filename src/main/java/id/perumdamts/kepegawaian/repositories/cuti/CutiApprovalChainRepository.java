package id.perumdamts.kepegawaian.repositories.cuti;

import id.perumdamts.kepegawaian.entities.cuti.CutiApprovalChain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CutiApprovalChainRepository extends JpaRepository<CutiApprovalChain, Long>,
        JpaSpecificationExecutor<CutiApprovalChain>,
        CutiApprovalChainCustomRepository {
    List<CutiApprovalChain> findByRefCuti_Id(Long id);

    List<CutiApprovalChain> findByRefCuti_IdOrderByApprovalLevelAsc(Long refCutiId);

    Optional<CutiApprovalChain> findByRefCutiIdAndJabatanId(Long refCutiId, Long jabatanId);
}
