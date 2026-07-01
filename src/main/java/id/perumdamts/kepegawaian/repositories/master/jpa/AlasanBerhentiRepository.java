package id.perumdamts.kepegawaian.repositories.master.jpa;

import id.perumdamts.kepegawaian.entities.master.AlasanBerhenti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

public interface AlasanBerhentiRepository extends
        JpaRepository<AlasanBerhenti, Long>,
        JpaSpecificationExecutor<AlasanBerhenti>,
        RevisionRepository<AlasanBerhenti, Long, Integer> {
}
