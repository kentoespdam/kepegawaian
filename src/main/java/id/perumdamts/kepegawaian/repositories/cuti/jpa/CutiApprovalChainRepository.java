package id.perumdamts.kepegawaian.repositories.cuti.jpa;

import id.perumdamts.kepegawaian.entities.cuti.CutiApprovalChain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CutiApprovalChainRepository extends JpaRepository<CutiApprovalChain, Long> {
    List<CutiApprovalChain> findByRefCuti_Id(Long id);

    Optional<CutiApprovalChain> findByRefCutiIdAndJabatanId(Long refCutiId, Long jabatanId);
}
