package id.perumdamts.kepegawaian.repositories.master.jpa;

import id.perumdamts.kepegawaian.entities.master.RumahDinas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

public interface RumahDinasRepository extends JpaRepository<RumahDinas, Long>,
RevisionRepository<RumahDinas, Long, Integer> {
    Optional<RumahDinas> findByNama(String nama);
}
