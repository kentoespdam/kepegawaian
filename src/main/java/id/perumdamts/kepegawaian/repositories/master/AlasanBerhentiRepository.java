package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.AlasanBerhenti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AlasanBerhentiRepository extends JpaRepository<AlasanBerhenti, Long>, JpaSpecificationExecutor<AlasanBerhenti> {
}
