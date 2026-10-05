package id.perumdamts.kepegawaian.repositories.master.jpa;

import id.perumdamts.kepegawaian.entities.master.AlasanBerhenti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

public interface AlasanBerhentiRepository extends
        JpaRepository<AlasanBerhenti, Long>,
RevisionRepository<AlasanBerhenti, Long, Integer> {
    Optional<AlasanBerhenti> findByNama(String nama);
}
