package id.perumdamts.kepegawaian.repositories.master.jpa;

import id.perumdamts.kepegawaian.entities.master.Golongan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

public interface GolonganRepository extends JpaRepository<Golongan, Long>,
RevisionRepository<Golongan, Long, Integer> {
    Optional<Golongan> findByGolonganAndPangkat(String golongan, String pangkat);
}
