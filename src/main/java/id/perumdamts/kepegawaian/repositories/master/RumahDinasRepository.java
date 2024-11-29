package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.RumahDinas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

public interface RumahDinasRepository extends JpaRepository<RumahDinas, Long>,
        JpaSpecificationExecutor<RumahDinas>,
        RevisionRepository<RumahDinas, Long, Long> {
}
