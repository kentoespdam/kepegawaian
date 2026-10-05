package id.perumdamts.kepegawaian.repositories.penggajian.jpa;

import id.perumdamts.kepegawaian.entities.penggajian.GajiPhdp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface GajiPhdpRepository extends JpaRepository<GajiPhdp, Long>,
RevisionRepository<GajiPhdp, Long, Integer> {
    boolean existsByKondisiAndFormula(String kondisi, String formula);
}
