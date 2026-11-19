package id.perumdamts.kepegawaian.repositories.cuti.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import id.perumdamts.kepegawaian.entities.cuti.CutiApproval;

public interface CutiApprovalRepository extends JpaRepository<CutiApproval, Long>,
        RevisionRepository<CutiApproval, Long, Integer> {
}
