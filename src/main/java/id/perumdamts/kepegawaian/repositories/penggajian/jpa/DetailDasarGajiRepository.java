package id.perumdamts.kepegawaian.repositories.penggajian.jpa;

import id.perumdamts.kepegawaian.entities.penggajian.DetailDasarGaji;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface DetailDasarGajiRepository extends JpaRepository<DetailDasarGaji, Long>,
RevisionRepository<DetailDasarGaji, Long, Integer> {
}
