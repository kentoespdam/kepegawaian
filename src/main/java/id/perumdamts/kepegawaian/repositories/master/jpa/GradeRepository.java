package id.perumdamts.kepegawaian.repositories.master.jpa;

import id.perumdamts.kepegawaian.entities.master.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long>,
RevisionRepository<Grade, Long, Integer> {
    Optional<Grade> findByLevelIdAndGrade(Long levelId, Integer grade);
}
