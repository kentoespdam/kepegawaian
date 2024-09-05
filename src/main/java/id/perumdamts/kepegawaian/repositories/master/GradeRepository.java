package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

public interface GradeRepository extends JpaRepository<Grade, Long>,
        JpaSpecificationExecutor<Grade>,
        RevisionRepository<Grade, Long, Long> {
}
