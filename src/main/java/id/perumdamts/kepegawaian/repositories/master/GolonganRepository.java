package id.perumdamts.kepegawaian.repositories.master;

import id.perumdamts.kepegawaian.entities.master.Golongan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;

public interface GolonganRepository extends JpaRepository<Golongan, Long>,
        JpaSpecificationExecutor<Golongan>,
        RevisionRepository<Golongan, Long, Long> {
}
