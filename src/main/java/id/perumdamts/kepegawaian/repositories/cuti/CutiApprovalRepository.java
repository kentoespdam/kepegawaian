package id.perumdamts.kepegawaian.repositories.cuti;

import id.perumdamts.kepegawaian.entities.cuti.CutiApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

public interface CutiApprovalRepository extends JpaRepository<CutiApproval, Long>,
        JpaSpecificationExecutor<CutiApproval>,
        RevisionRepository<CutiApproval, Long, Long> {
}
