package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.Sanksi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

public interface SanksiRepository extends JpaRepository<Sanksi, Long>,
        JpaSpecificationExecutor<Sanksi>,
        RevisionRepository<Sanksi, Long, Long> {
}
